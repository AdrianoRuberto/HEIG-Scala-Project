package game.server

import game.server.actors.Watcher

abstract class BasicBot(name: String) extends BasicActor(name) {
	final def receive: Receive = ({
		/*case ServerMessage.GameFound(mode, team, me, warmup) =>
		case ServerMessage.GameStart => start()
		case ServerMessage.GameEnd =>*/
		case true =>
	}: Receive) orElse message

	init()

	def init(): Unit
	def start(): Unit
	def message: Receive

	def terminate(): Unit = context.parent ! Watcher.Terminate
}
