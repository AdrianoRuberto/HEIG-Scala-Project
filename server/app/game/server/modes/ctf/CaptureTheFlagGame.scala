package game.server.modes.ctf

import engine.geometry.{Circle, Vector2D}
import game.UID
import game.doodads.Doodad
import game.server._
import game.server.behaviors.StandardDeathBehavior
import game.skeleton.Skeleton
import game.spells.Spell
import utils.Color

/**
  * Capture the enemy team's flag while defending your
  */
class CaptureTheFlagGame (roster: Seq[GameTeam]) extends BasicGame(roster) with StandardDeathBehavior {
	private val map = GameMap.Nepal

	loadMap(map)
	setDefaultTeamColors()
	setDefaultCamera()

	private final val GameDuration = 5 * 60 // seconds
	private final val pointA = Vector2D(-2000, 0)
	private final val pointB = Vector2D(2000, 0)

	// Teams
	private val Seq(teamA, teamB) = teams

	private val colorOff = Color(150, 150, 150)
	private val colorA = teamA.color
	private val colorB = teamB.color

	// Base spells
	for (player <- players) {
		player gainSpell (0, Spell.Sword)
		player gainSpell (3, Spell.Sprint)
		player gainSpell (2, Spell.BioticField)
	}

	private val status = createDynamicDoodad(Doodad.Hud.CtfStatus, Skeleton.CtfStatus)
	status.timer.value = GameDuration
	status.teamA.value = teamA
	status.teamB.value = teamB
	status.colorA.value = colorA
	status.colorB.value = colorB

	private val shapeA = Circle(pointA, 80)
	private val regionA = createRegion(shapeA, enters = enterArea(teamA), filter = areaFilter(teamA))
	private val areaA = createDynamicDoodad(Doodad.Area.DynamicArea, Skeleton.DynamicArea)
	areaA.shape.value = shapeA
	areaA.setColor(colorA)

	private val shapeB = Circle(pointB, 80)
	private val regionB = createRegion(shapeB, enters = enterArea(teamB), filter = areaFilter(teamB))
	private val areaB = createDynamicDoodad(Doodad.Area.DynamicArea, Skeleton.DynamicArea)
	areaB.shape.value = shapeB
	areaB.setColor(colorB)

	private var flagA = createDoodad(Doodad.Spell.Flag(pointA, None, colorA))
	private var flagAHolder: UID = UID.zero

	private var flagB = createDoodad(Doodad.Spell.Flag(pointB, None, colorB))
	private var flagBHolder: UID = UID.zero

	def setTeamFlagLocation(team: UID, location: Vector2D): Unit = {
		if (team == teamA) {
			flagAHolder = UID.zero
			if (location == pointA) {
				status.controllingA.value = true
				areaA.setColor(colorA)
			}
			flagA.remove()
			flagA = createDoodad(Doodad.Spell.Flag(location, None, colorA))
		} else {
			flagBHolder = UID.zero
			if (location == pointB) {
				status.controllingB.value = true
				areaB.setColor(colorB)
			}
			flagB.remove()
			flagB = createDoodad(Doodad.Spell.Flag(location, None, colorB))
		}
	}

	def setTeamFlagHolder(team: UID, holder: UID): Unit = {
		if (team == teamA) {
			if (status.controllingA.value) {
				status.controllingA.value = false
				areaA.setColor(colorOff)
			}
			flagAHolder = holder
			flagA.remove()
			flagA = createDoodad(Doodad.Spell.Flag(Vector2D.zero, Some(holder.skeleton.uid), colorA))
		} else {
			if (status.controllingB.value) {
				status.controllingB.value = false
				areaB.setColor(colorOff)
			}
			flagBHolder = holder
			flagB.remove()
			flagB = createDoodad(Doodad.Spell.Flag(Vector2D.zero, Some(holder.skeleton.uid), colorB))
		}
	}

	private def areaFilter(team: UID)(player: UID): Boolean = {
		// Taking flag
		val playerIsOpponent = player.team != team
		val ourFlagIsAtBase = (if (team == teamA) flagAHolder else flagBHolder) == UID.zero
		val takeFlag = playerIsOpponent && ourFlagIsAtBase

		// Dropping flag
		val playerIsOtherTeamHolder = (if (team == teamA) flagBHolder else flagAHolder) == player
		val ourFlagIsControlled = (if (team == teamA) flagAHolder else flagBHolder) == UID.zero
		val dropFlag = playerIsOtherTeamHolder && ourFlagIsControlled

		// Any of the two
		takeFlag || dropFlag
	}

	private def enterArea(team: UID)(player: UID): Unit = {
		if (player.team == team) {
			// Dropping flag
			dropFlag(player, scoring = true)
			if (team == teamA) {
				status.scoreA.value += 1
				setTeamFlagLocation(teamB, pointB)
			} else {
				status.scoreB.value += 1
				setTeamFlagLocation(teamA, pointA)
			}
		} else {
			// Taking flag
			pickupFlag(player)
		}

		if (status.scoreA.value == 3 || status.scoreB.value == 3) {
			end()
		}
	}

	private def pickupFlag(player: UID): Unit = {
		player gainSpell (1, Spell.DropTheFlag)
		player.skeleton.speed.value /= 1.25
		setTeamFlagHolder(if (player.team == teamA) teamB else teamA, player)
	}

	private var lastDropA = (UID.zero, 0.0)
	private var lastDropB = (UID.zero, 0.0)

	class PickupRegion (location: Vector2D, flagTeam: UID, color: Color) {
		private val area = Circle(location, 15)
		private val doodad = createDoodad(Doodad.Area.StaticArea(area, fillColor = color * 0.1, strokeColor = color * 0.8))
		private val region: Region = createRegion(area, enters = pickup, filter = canPickup)

		def canPickup(player: UID): Boolean = {
			if (player.team == flagTeam) true
			else {
				val (lastDropPlayer, lastDropTime) = if (player.team == teamA) lastDropA else lastDropB
				lastDropPlayer != player || time - lastDropTime > 1000
			}
		}

		def pickup(player: UID): Unit = {
			doodad.remove()
			region.remove()
			if (player.team != flagTeam) {
				pickupFlag(player)
			} else {
				setTeamFlagLocation(player.team, if (player.team == teamA) pointA else pointB)
			}
		}
	}

	def dropFlag(player: UID, scoring: Boolean = false): Unit = if (player == flagBHolder || player == flagAHolder) {
		player loseSpell 1
		player.skeleton.speed.value *= 1.25

		if (!scoring) {
			val location = player.skeleton.position
			new PickupRegion(
				location,
				flagTeam = if (player.team == teamA) teamB else teamA,
				color = if (player.team == teamA) colorB else colorA
			)

			if (player.team == teamA) lastDropA = (player, time)
			else lastDropB = (player, time)

			setTeamFlagLocation(if (player.team == teamA) teamB else teamA, location)
		}
	}

	override def playerDeath(player: UID): Unit = dropFlag(player)

	private var gameEndTask: ScheduledTask = null

	override def start(): Unit = {
		status.timer.interpolateAtSpeed(0, 1)
		gameEndTask = schedule(GameDuration * 1000)(end())
	}

	def end(): Unit = {
		removeDeathScreens()
		gameEndTask.cancel()
		players.engine.disableInputs()

		if (status.scoreA.value > status.scoreB.value) {
			players.camera.setSmoothing(true)
			players.camera.move(pointA)
			createDoodad(Doodad.Hud.VictoryScreen("Blue team wins!", "rgb(119, 119, 255)"))
		} else if (status.scoreB.value > status.scoreA.value) {
			players.camera.setSmoothing(true)
			players.camera.move(pointB)
			createDoodad(Doodad.Hud.VictoryScreen("Red team wins!", "rgb(255, 85, 85)"))
		} else {
			createDoodad(Doodad.Hud.VictoryScreen("Draw!", "#999"))
		}

		terminate(5000)
	}

	def respawnLocation(player: UID): Vector2D = map.spawns(teamsIndex(player.team))
}
