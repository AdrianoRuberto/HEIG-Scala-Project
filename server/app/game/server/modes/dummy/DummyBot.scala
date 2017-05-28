package game.server.modes.dummy

import game.server.BasicBot

class DummyBot(name: String) extends BasicBot(name) {
	def init(): Unit = ()
	def start(): Unit = ()
	def message: Receive = {
		case _ =>
	}
}
