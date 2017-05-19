package actors

import actors.Matchmaker.QueuedPlayer
import akka.actor.{Actor, ActorRef, Terminated}
import game.{Player, ServerMessage, UID}
import modes.GameBuilder
import scala.annotation.tailrec
import scala.collection.immutable.TreeSet
import scala.concurrent.duration._
import utils.NameGenerator

class Matchmaker extends Actor {
	import context._

	private var queue = TreeSet.empty[QueuedPlayer](Ordering.by(_.since))
	private var players = Map.empty[ActorRef, QueuedPlayer]
	private var lastQueueSize = 0

	def receive: Receive = {
		case Matchmaker.Register(player) =>
			val qplayer = QueuedPlayer(sender, player.copy(), Deadline.now)
			queue += qplayer
			players += (sender -> qplayer)
			context.watch(sender)
			sender ! ServerMessage.QueueUpdate(lastQueueSize)
			if (queue.size == 1) system.scheduler.scheduleOnce(500.millis, self, Matchmaker.Tick)

		case Terminated(actor) =>
			for (qplayer <- players.get(actor)) {
				queue -= qplayer
				players -= actor
			}

		case Matchmaker.Tick =>
			if (queue.nonEmpty) tick()
			if (queue.nonEmpty) system.scheduler.scheduleOnce(2.seconds, self, Matchmaker.Tick)
			if (lastQueueSize != queue.size) {
				for (QueuedPlayer(actor, _, _) <- queue) actor ! ServerMessage.QueueUpdate(queue.size)
				lastQueueSize = queue.size
			}
	}

	private def pick(n: Int): Vector[QueuedPlayer] = {
		queue.splitAt(n) match {
			case (picked, rest) =>
				queue = rest
				picked.toVector
		}
	}

	@tailrec
	private def tick(): Unit = {
		val builder = GameBuilder.random
		def shouldFillWithBots: Boolean = builder.bot.isDefined && queue.head.waitBound.isOverdue()
		if (queue.size >= builder.players || shouldFillWithBots) {
			var players = pick(builder.players)
			if (players.size < builder.players) {
				players ++= Vector.fill(builder.players - players.size) {
					val botPlayer = Player(NameGenerator.generate, UID.next, bot = true)
					QueuedPlayer(system.deadLetters, botPlayer, Deadline.now)
				}
			}
			val teams = builder.compose(players.map(_.player))
			for (QueuedPlayer(actor, Player(_, uid, bot), _) <- players if !bot) {
				actor ! ServerMessage.GameFound(builder.mode, teams, uid)
			}
			if (queue.nonEmpty) tick()
		}
	}
}

object Matchmaker {
	case class Register(player: Player)
	case object Tick

	/** A queued player with some metadata */
	case class QueuedPlayer(actor: ActorRef, player: Player, since: Deadline) {
		val waitBound: Deadline = since + 15.seconds
	}
}
