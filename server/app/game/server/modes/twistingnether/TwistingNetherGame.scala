package game.server.modes.twistingnether

import engine.geometry.Rectangle
import game.UID
import game.doodads.Doodad
import game.maps.GameMap
import game.server.{BasicGame, GameTeam}
import game.skeleton.SkeletonType
import game.skeleton.concrete.PointSkeleton
import game.spells.Spell

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	log("TN: Game init")

	loadMap(GameMap.Illios)
	setDefaultTeamColors()
	setDefaultCamera()

	private val Seq(teamA, teamB) = teams

	private val status = createGlobalSkeleton(SkeletonType.KothStatus)
	status.teamA.value = teamA
	status.teamB.value = teamB

	createGlobalDoodad(Doodad.Status.Koth(status.uid))

	var playerFromAOnPoint = 0
	var playerFromBOnPoint = 0

	val captureArea = Rectangle(-150, 350, 300, 300)

	def enterArea(uid: UID): Unit = {
		if (uid.team == teamA) playerFromAOnPoint += 1
		else if (uid.team == teamB) playerFromBOnPoint += 1
	}

	def leaveArea(uid: UID): Unit = {
		if (uid.team == teamA) playerFromAOnPoint -= 1
		else if (uid.team == teamB) playerFromBOnPoint -= 1
	}

	createRegion(captureArea, enterArea, leaveArea)

	createTicker { dt =>
		if (playerFromAOnPoint > 0 && playerFromBOnPoint == 0) {
			if (status.controlling.value != teamA) {
				status.capture.value = (status.capture.value + dt / 50 ) min 100
			}
		} else if (playerFromBOnPoint > 0 && playerFromAOnPoint == 0) {
			if (status.controlling.value != teamB) {
				status.capture.value = (status.capture.value - dt / 50) max -100
			}
		}

		val captureValue = status.capture.value
		if (captureValue == 100) status.controlling.value = teamA
		else if (captureValue == -100) status.controlling.value = teamB

		if (status.controlling.value == teamA) status.progressA.value += dt / 1000
		else if (status.controlling.value == teamB) status.progressB.value += dt / 1000
	}

	// DEBUG
	private var points = Map.empty[UID, PointSkeleton]

	for (player <- players) {
		player gainSpell (0, Spell.Sword)
		player gainSpell (3, Spell.Sprint)

		// DEBUG
		val ps = createGlobalSkeleton(SkeletonType.Point)
		ps.color.value = player.skeleton.color.value
		points += (player -> ps)
		createGlobalDoodad(Doodad.Debug.Point(ps.uid))
	}

	// DEBUG
	createTicker { _ =>
		for (player <- players) {
			val ps = points(player)
			val pos = player.skeleton.position
			ps.x.value = pos.x
			ps.y.value = pos.y
		}
	}

	def start(): Unit = {
		log("TN: Game start")
	}
}
