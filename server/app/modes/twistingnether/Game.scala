package modes
package twistingnether

import actors.Watcher
import scala.concurrent.duration._

class Game (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = ()
	def start(): Unit = {
		context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case _ =>
	}
}
