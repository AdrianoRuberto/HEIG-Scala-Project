package game.server.modes.twistingnether

import engine.geometry.{Rectangle, Vector2D}
import game.UID
import game.doodads.Doodad
import game.maps.GameMap
import game.server.behaviors.StandardDeathBehavior
import game.server.{BasicGame, GameTeam}
import game.skeleton.SkeletonType
import game.skeleton.concrete.PointSkeleton
import game.spells.Spell

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) with StandardDeathBehavior {
	loadMap(GameMap.Illios)
	setDefaultTeamColors()
	setDefaultCamera()

	private val Seq(teamA, teamB) = teams

	private val status = createGlobalSkeleton(SkeletonType.KothStatus)
	status.teamA.value = teamA
	status.teamB.value = teamB

	createGlobalDoodad(Doodad.Interface.Koth(status.uid))

	private var playerFromAOnPoint = 0
	private var playerFromBOnPoint = 0

	private val captureArea = Rectangle(-145, 355, 290, 290)
	createRegion(captureArea, enterArea, leaveArea)

	private val areaSkeleton = createGlobalSkeleton(SkeletonType.DynamicArea)
	areaSkeleton.shape.value = captureArea
	areaSkeleton.strokeWidth.value = 2

	private val areaDoodad = createGlobalDoodad(Doodad.Area.DynamicArea(areaSkeleton.uid))

	private def enterArea(uid: UID): Unit = {
		if (uid.team == teamA) playerFromAOnPoint += 1
		else if (uid.team == teamB) playerFromBOnPoint += 1
	}

	private def leaveArea(uid: UID): Unit = {
		if (uid.team == teamA) playerFromAOnPoint -= 1
		else if (uid.team == teamB) playerFromBOnPoint -= 1
	}

	private final val CapturePerSecond = 20.0
	private final val ProgressPerSecond = 1.0

	// Capture progress
	private var currentCapture = 0
	private def interpolateCapture(direction: Int): Unit = if (currentCapture != direction) {
		currentCapture = direction
		status.capture.interpolateAtSpeed(direction * 100, CapturePerSecond)
	}

	private val areaTicker = createTicker { dt =>
		// Current point controller
		val controlling = status.controlling.value

		// Capture cases
		if (playerFromAOnPoint > 0 && playerFromBOnPoint == 0 && controlling != teamA) {
			// Team A captures the point
			interpolateCapture(1)
		} else if (playerFromBOnPoint > 0 && playerFromAOnPoint == 0 && controlling != teamB) {
			// Team B captures the point
			interpolateCapture(-1)
		} else if (playerFromBOnPoint == 0 && controlling == teamA) {
			// Capture decays toward team A
			interpolateCapture(1)
		} else if (playerFromAOnPoint == 0 && controlling == teamB) {
			// Capture decays toward team B
			interpolateCapture(-1)
		} else if (playerFromAOnPoint == 0 && playerFromBOnPoint == 0 && controlling == UID.zero) {
			// Capture decays toward neutral
			interpolateCapture(0)
		} else {
			// The point is contested in any way
			status.capture.stop()
		}

		// Point capture triggers
		val captureValue = status.capture.current
		if (captureValue == 100 && controlling != teamA) {
			// Team A took the point
			status.controlling.value = teamA
			areaSkeleton.fillColor.value = "rgba(119, 119, 255, 0.1)"
			areaSkeleton.strokeColor.value = "rgba(119, 119, 255, 0.8)"
			status.progressA.interpolateAtSpeed(100, ProgressPerSecond)
			status.progressB.stop()
		} else if (captureValue == -100 && controlling != teamB) {
			// Team B took the point
			status.controlling.value = teamB
			areaSkeleton.fillColor.value = "rgba(255, 85, 85, 0.1)"
			areaSkeleton.strokeColor.value = "rgba(255, 85, 85, 0.8)"
			status.progressB.interpolateAtSpeed(100, ProgressPerSecond)
			status.progressA.stop()
		}

		// Win condition
		if (status.progressA.value >= 100) win(teamA)
		else if (status.progressB.value >= 100) win(teamB)
	}

	def win(team: UID): Unit = {
		areaTicker.remove()
	}

	// DEBUG
	private var points = Map.empty[UID, PointSkeleton]

	for (player <- players) {
		player gainSpell (0, Spell.Sword)
		player gainSpell (3, Spell.Sprint)
		player gainSpell (1, Spell.Flagellation)
		player gainSpell (2, Spell.BioticField)

		// DEBUG
		/*val ps = createGlobalSkeleton(SkeletonType.Point)
		ps.color.value = player.skeleton.color.value
		points += (player -> ps)
		createGlobalDoodad(Doodad.Debug.Point(ps.uid))*/
	}

	// DEBUG
	/*createTicker { _ =>
		for (player <- players) {
			val ps = points(player)
			val pos = player.skeleton.position
			ps.x.value = pos.x
			ps.y.value = pos.y
		}
	}*/

	def start(): Unit = ()
	def respawnLocationForPlayer(player: UID): Vector2D = Vector2D(0, 0)
}
