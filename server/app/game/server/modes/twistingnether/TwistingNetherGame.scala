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
	log("TN: Game init")

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

	private final val TimeForCapture = 1000
	private final val TimePerPercent = 1000

	private val areaTicker = createTicker { dt =>
		val controlling = status.controlling.value
		val capture = status.capture.value
		// Capture progress
		def captureProgress(delta: Double): Unit = {
			val updated = status.capture.value + 100 * delta / TimeForCapture
			status.capture.value = if (delta < 0) updated max -100 else updated min 100
		}
		if (playerFromAOnPoint > 0 && playerFromBOnPoint == 0 && controlling != teamA) {
			captureProgress(dt)
		} else if (playerFromBOnPoint > 0 && playerFromAOnPoint == 0 && controlling != teamB) {
			captureProgress(-dt)
		} else if (playerFromBOnPoint == 0 && controlling == teamA && capture != 100) {
			captureProgress(dt)
		} else if (playerFromAOnPoint == 0 && controlling == teamB && capture != -100) {
			captureProgress(-dt)
		} else if (playerFromAOnPoint == 0 && playerFromBOnPoint == 0 && controlling == UID.zero && capture != 0) {
			val cap = -capture * TimeForCapture / 100
			if (capture < 0) captureProgress(dt min cap) else captureProgress(-dt max cap)
		}

		// Point captured
		val captureValue = status.capture.value
		if (captureValue == 100) {
			status.controlling.value = teamA
			areaSkeleton.fillColor.value = "rgba(119, 119, 255, 0.1)"
			areaSkeleton.strokeColor.value = "rgba(119, 119, 255, 0.8)"
		} else if (captureValue == -100) {
			status.controlling.value = teamB
			areaSkeleton.fillColor.value = "rgba(255, 85, 85, 0.1)"
			areaSkeleton.strokeColor.value = "rgba(255, 85, 85, 0.8)"
		}

		// Score progression
		if (status.controlling.value == teamA) status.progressA.value += dt / TimePerPercent
		else if (status.controlling.value == teamB) status.progressB.value += dt / TimePerPercent

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

	def start(): Unit = {
		log("TN: Game start")
	}
	def respawnLocationForPlayer(player: UID): Vector2D = Vector2D(0, 0)
}
