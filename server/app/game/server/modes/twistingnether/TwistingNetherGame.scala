package game.server.modes.twistingnether

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
	camera.followSelf()
	camera.setSpeed(250)

	var points = Map.empty[UID, PointSkeleton]

	for (player <- players) {
		player gainSpell (0, Spell.Sword)
		player gainSpell (3, Spell.Sprint)

		val ps = createGlobalSkeleton(SkeletonType.Point)
		ps.color.value = player.skeleton.color.value
		points += (player -> ps)
		createGlobalDoodad(Doodad.Debug.Point(ps.uid))
	}

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

		//warn("FIXME: Game will shutdown in 5 seconds")
		//context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case null => ???
	}

	def tick(dt: Double): Unit = ()
}
