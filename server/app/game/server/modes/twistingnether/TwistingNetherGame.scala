package game.server.modes.twistingnether

import game.maps.GameMap
import game.server.actors.Watcher
import game.server.{BasicGame, GameTeam}
import scala.concurrent.duration._

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = {
		log("Game init")
		loadMap(GameMap.Illios)
		camera.followSelf()
	}

	def start(): Unit = {
		log("Game start")
		warn("FIXME: Game will shutdown in 5 seconds")
		camera.setSpeed(20)
		context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case m => error("Unable to handle message", m.toString)
	}
}
