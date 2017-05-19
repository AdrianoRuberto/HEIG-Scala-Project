package actors

import actors.Matchmaker.QueuedPlayer
import akka.actor.{Actor, ActorRef, Terminated}
import game.{PlayerInfo, ServerMessage, UID}
import modes.{GameBuilder, GamePlayer}
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

	@tailrec
	private def tick(): Unit = {
		val builder = GameBuilder.random
		def shouldFillWithBots: Boolean = builder.canSpawnBots && queue.head.waitBound.isOverdue()
		if (queue.size >= builder.players || shouldFillWithBots) {
			val players = pickAndFill(builder, builder.players)
			val teams = builder.composeTeams(players)
			for (player <- players) {
				player.actor ! ServerMessage.GameFound(builder.mode, teams.map(_.info), player.info.uid)
			}
			builder.instantiate(teams)
			if (queue.nonEmpty) tick()
		}
	}

	private def pickAndFill(builder: GameBuilder, count: Int): Seq[GamePlayer] = {
		val humans = pick(count)
		if (humans.size < count) humans ++ buildBots(builder, count - humans.size)
		else humans
	}

	private def pick(n: Int): Seq[GamePlayer] = {
		queue.splitAt(n) match {
			case (picked, rest) =>
				queue = rest
				picked.map {
					case QueuedPlayer(actor, info, _) => GamePlayer(actor, info)
				}.toSeq
		}
	}

	private def buildBots(builder: GameBuilder, count: Int): Seq[GamePlayer] = {
		Seq.fill(count) {
			GamePlayer(builder.spawnBot(), PlayerInfo(UID.next, NameGenerator.generate, bot = true))
		}
	}
}

object Matchmaker {
	case class Register(player: PlayerInfo)
	case object Tick

	/** A queued player with some metadata */
	case class QueuedPlayer(actor: ActorRef, player: PlayerInfo, since: Deadline) {
		val waitBound: Deadline = since + 15.seconds
	}
}
