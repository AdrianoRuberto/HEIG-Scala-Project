package actors

import akka.actor.{Actor, ActorRef, Terminated}
import game.{Player, ServerMessage, Team}
import scala.collection.SortedSet
import scala.compat.Platform
import scala.util.Random

class Matchmaker extends Actor {
	case class QueuedPlayer(actor: ActorRef, player: Player, since: Long)

	private var queue = SortedSet.empty[QueuedPlayer](Ordering.by(_.since))
	private var players = Map.empty[ActorRef, QueuedPlayer]
	private var lastQueueSize = 0

	def receive: Receive = {
		case Matchmaker.Register(player) =>
			val qplayer = QueuedPlayer(sender, player, Platform.currentTime)
			queue += qplayer
			players += (sender -> qplayer)
			context.watch(sender)
			tick()

		case Terminated(actor) =>
			for (qplayer <- players.get(actor)) {
				queue -= qplayer
				players -= actor
				updateQueueSize()
			}
	}

	def tick(): Unit = {
		if (queue.size >= 2) {
			val (roster, rest) = queue.splitAt(2)
			queue = rest
			var teams: List[Team] = Nil
			for (QueuedPlayer(actor, player, _) <- Random.shuffle(roster.toVector)) {
				players -= actor
				context.unwatch(actor)
				teams = Team(Seq(player)) :: teams
			}
			for (QueuedPlayer(actor, _, _) <- roster) {
				actor ! ServerMessage.GameFound(teams)
			}
		}
		updateQueueSize()
	}

	def updateQueueSize(): Unit = {
		if (lastQueueSize != queue.size) {
			for (QueuedPlayer(actor, _, _) <- queue) actor ! ServerMessage.QueueUpdate(queue.size)
			lastQueueSize = queue.size
		}
	}
}

object Matchmaker {
	case class Register(player: Player)
}
