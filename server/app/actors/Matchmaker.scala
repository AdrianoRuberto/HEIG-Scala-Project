package actors

import akka.actor.Actor
import game.{Player, ServerMessage}

class Matchmaker extends Actor {
	def receive: Receive = {
		case Matchmaker.Register(player) =>
			println(player, "register to matchmaker")
			sender ! ServerMessage.GameFound(null)
	}
}

object Matchmaker {
	case class Register(player: Player)
}
