package game.modes
package twistingnether

import actors.Watcher
import scala.concurrent.duration._

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = {
		log("Game init")
	}

	def start(): Unit = {
		log("Game start")
		warn("There is no game impl, game will shut down in 5 sec")
		context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
		???
	}

	def message: Receive = {
		case m => error("Unable to handle message", m.toString)
	}
}
