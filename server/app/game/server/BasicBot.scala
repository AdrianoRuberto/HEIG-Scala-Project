package game.server

import game.protocol.ServerMessage
import game.server.actors.{Matchmaker, Watcher}

abstract class BasicBot(name: String) extends BasicActor(name) {
	final def receive: Receive = ({
		case ServerMessage.GameFound(mode, team, me, warmup) =>
			sender() ! Matchmaker.Ready
	}: Receive) orElse message

	init()

	def init(): Unit
	def start(): Unit
	def message: Receive

	def terminate(): Unit = context.parent ! Watcher.Terminate
}
