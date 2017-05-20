package game.modes
package dummy

class DummyBot(name: String) extends BasicBot(name) {
	def init(): Unit = ()
	def start(): Unit = ()
	def message: Receive = {
		case _ =>
	}
}
