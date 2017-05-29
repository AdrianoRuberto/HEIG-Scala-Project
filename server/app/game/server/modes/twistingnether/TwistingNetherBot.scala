package game.server.modes.twistingnether

import game.server.BasicBot

class TwistingNetherBot(name: String) extends BasicBot(name: String) {
	def init(): Unit = ()
	def start(): Unit = ()
	def message: Receive = {
		case msg => //debug(msg.toString)
	}
}
