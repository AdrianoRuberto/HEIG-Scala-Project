package game.modes
package twistingnether

import actors.Watcher
import game.maps.GameMap
import game.protocol.ServerMessage
import scala.concurrent.duration._

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = {
		log("Game init")
		players.values.foreach(_.actor ! ServerMessage.SetGameMap(GameMap.Illios))
	}

	def start(): Unit = {
		log("Game start")
		warn("FIXME: Game will shutdown in 5 seconds")
		context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case m => error("Unable to handle message", m.toString)
	}
}
