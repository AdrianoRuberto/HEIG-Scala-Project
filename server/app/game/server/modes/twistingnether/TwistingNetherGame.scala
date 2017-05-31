package game.server.modes.twistingnether

import game.maps.GameMap
import game.server.actors.Watcher
import game.server.{BasicGame, GameTeam}
import scala.concurrent.duration._

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = {
		log("TN: Game init")
		loadMap(GameMap.Illios)
		camera.followSelf()
	}

	def start(): Unit = {
		log("TN: Game start")
		camera.setSpeed(120)

		skeletons.head._2.x.interpolate(0, 5000)
		skeletons.head._2.y.interpolate(0, 5000)

		warn("FIXME: Game will shutdown in 5 seconds")
		context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case null => ???
	}
}
