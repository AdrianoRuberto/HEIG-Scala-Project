package actors

import akka.actor.{Actor, ActorRef}
import game.{Player, ServerMessage}
import scala.collection.SortedSet
import scala.compat.Platform

class Matchmaker extends Actor {
	case class QueuedPlayer(actor: ActorRef, player: Player, since: Long)
	private var queue = SortedSet.empty[QueuedPlayer](Ordering.by(_.since))

	def receive: Receive = {
		case Matchmaker.Register(player) =>
			queue += QueuedPlayer(sender, player, Platform.currentTime)
			for (QueuedPlayer(actor, _, _) <- queue) actor ! ServerMessage.QueueUpdate(queue.size)
	}
}

object Matchmaker {
	case class Register(player: Player)
}
