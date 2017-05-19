package actors

import actors.Matchmaker.{GameMode, QueuedPlayer}
import akka.actor.{Actor, ActorRef, Terminated}
import game.{Player, ServerMessage, Team}
import scala.annotation.tailrec
import scala.collection.SortedSet
import scala.concurrent.duration._
import scala.util.Random
import utils.NameGenerator

class Matchmaker extends Actor {
	import context._

	private var queue = SortedSet.empty[QueuedPlayer](Ordering.by(_.since))
	private var players = Map.empty[ActorRef, QueuedPlayer]
	private var lastQueueSize = 0

	def receive: Receive = {
		case Matchmaker.Register(player) =>
			val qplayer = QueuedPlayer(sender, player.copy(uid = Matchmaker.nextPlayerUID), Deadline.now)
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

	@tailrec
	private def tick(): Unit = {
		val mode = GameMode.random
		def shouldFillWithBots: Boolean = mode.bot.isDefined && queue.head.unacceptableWait
		if (queue.size >= mode.players || shouldFillWithBots) {
			var players = queue.splitAt(mode.players) match {
				case (plys, rest) => queue = rest; plys.toVector
			}
			if (players.size < mode.players) {
				players ++= Vector.fill(mode.players - players.size) {
					val botPlayer = Player(NameGenerator.generate, bot = true, Matchmaker.nextPlayerUID)
					QueuedPlayer(system.deadLetters, botPlayer, Deadline.now)
				}
			}
			val teams = mode.build(players.map(_.player))
			for (QueuedPlayer(actor, Player(_, bot, uid), _) <- players if !bot) {
				actor ! ServerMessage.GameFound(teams, uid)
			}
			tick()
		}
	}
}

object Matchmaker {
	private var lastPlayerUID: Long = 0

	def nextPlayerUID: Long = {
		lastPlayerUID += 1
		lastPlayerUID
	}

	case class Register(player: Player)
	case object Tick

	case class QueuedPlayer(actor: ActorRef, player: Player, since: Deadline) {
		def unacceptableWait: Boolean = (since + 15.seconds).isOverdue()
	}

	abstract class GameMode(val players: Int, val bot: Option[Any]) {
		def build(players: Seq[Player]): Seq[Team]
	}

	object GameMode {
		val modes = Vector(Foobar)
		def random: GameMode = modes(Random.nextInt(modes.size))
	}

	object Foobar extends GameMode(6, Some(null)) {
		def build(players: Seq[Player]): Seq[Team] = Random.shuffle(players).grouped(3).map(Team.apply).toSeq
	}
}
