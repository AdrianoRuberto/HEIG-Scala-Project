package game.modes
package dummy

class DummyBot extends BasicBot {
	def init(): Unit = ()
	def start(): Unit = ()
	def message: Receive = {
		case _ =>
	}
}
