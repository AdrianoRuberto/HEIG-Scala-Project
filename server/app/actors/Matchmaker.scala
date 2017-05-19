package actors

import actors.Matchmaker.QueuedPlayer
import akka.actor.{Actor, ActorRef, Terminated}
import game.{PlayerInfo, ServerMessage, UID}
import modes.twistingnether.TwistingNether
import modes.{GameBuilder, GamePlayer}
import scala.annotation.tailrec
import scala.collection.immutable.TreeSet
import scala.concurrent.duration._
import utils.NameGenerator

class Matchmaker extends Actor {
	import context._

	private var queue = TreeSet.empty[QueuedPlayer](Ordering.by(_.since))
	private var players = Map.empty[ActorRef, QueuedPlayer]

	private var ticking = false
	private var lastQueueSize = 0
	private var maxQueueSize = 0
	private var queueStabilityIndex = 0

	def receive: Receive = {
		case Matchmaker.Register(player, true) =>
			val mode = TwistingNether
			val spots = mode.playerSpots(1)
			build(mode, Seq(GamePlayer(sender, player)) ++ buildBots(mode, spots - 1))

		case Matchmaker.Register(player, false) =>
			val queued = QueuedPlayer(sender, player, Deadline.now)
			queue += queued
			players += (sender -> queued)
			context.watch(sender)
			sender ! ServerMessage.QueueUpdate(lastQueueSize)
			if (!ticking) {
				system.scheduler.scheduleOnce(500.millis, self, Matchmaker.Tick)
				ticking = true
			}

		case Terminated(actor) =>
			for (queued <- players.get(actor)) {
				queue -= queued
				players -= actor
			}

		case Matchmaker.Tick =>
			// Current number of player waiting for a game
			val currentQueueSize = queue.size

			// Update the queue stability counter
			if (currentQueueSize > maxQueueSize) {
				maxQueueSize = currentQueueSize
				queueStabilityIndex = 0
			} else if (queueStabilityIndex < 10) {
				queueStabilityIndex += 1
			}

			// Perform the tick if appropriate
			if (queue.nonEmpty) tick()

			// Schedule the next tick if there is still player waiting
			if (queue.nonEmpty) system.scheduler.scheduleOnce(1.seconds, self, Matchmaker.Tick)
			else ticking = false

			// Update queue size for everyone
			if (lastQueueSize != currentQueueSize) {
				for (QueuedPlayer(actor, _, _) <- queue) actor ! ServerMessage.QueueUpdate(queue.size)
				lastQueueSize = currentQueueSize
			}
	}

	@tailrec
	private def tick(): Unit = {
		// The queue is stable for 5 sec or more and the warmup time is expired
		val stableQueue = queue.head.warmupTime.isOverdue() && queueStabilityIndex >= 5
		// The queue is lengthy, no point in waiting for more players
		val lengthyQueue = queue.size > 20

		if (stableQueue || lengthyQueue) {
			val builder = GameBuilder.random
			val spots = builder.playerSpots(queue.size)
			if (queue.size >= spots || queue.head.meltingTime.isOverdue()) {
				val players = pickAndFill(builder, spots)
				build(builder, players)
				maxQueueSize = queue.size
				queueStabilityIndex -= 1
				if (queue.nonEmpty) tick()
			}
		}
	}

	private def build(builder: GameBuilder, players: Seq[GamePlayer]): Unit = {
		val teams = builder.composeTeams(players)
		val warmup = builder.warmup(players.size)
		for (player <- players) {
			player.actor ! ServerMessage.GameFound(builder.mode, teams.map(_.info), player.info.uid, warmup)
		}
		val game = builder.instantiate(teams)
		system.scheduler.scheduleOnce(warmup.seconds) {
			for (player <- players) player.actor ! ServerMessage.GameStart
			game ! Matchmaker.Start
		}
	}

	private def pickAndFill(builder: GameBuilder, spots: Int): Seq[GamePlayer] = {
		val humans = pick(spots)
		if (humans.size < spots) humans ++ buildBots(builder, spots - humans.size)
		else humans
	}

	private def pick(spots: Int): Seq[GamePlayer] = {
		queue.splitAt(spots) match {
			case (picked, rest) =>
				queue = rest
				picked.map {
					case QueuedPlayer(actor, info, _) => GamePlayer(actor, info)
				}.toSeq
		}
	}

	private def buildBots(builder: GameBuilder, spots: Int): Seq[GamePlayer] = {
		Seq.fill(spots) {
			GamePlayer(builder.spawnBot(), PlayerInfo(UID.next, NameGenerator.generate, bot = true))
		}
	}
}

object Matchmaker {
	case class Register(player: PlayerInfo, fast: Boolean)
	case object Tick
	case object Start

	/** A queued player with some metadata */
	case class QueuedPlayer(actor: ActorRef, player: PlayerInfo, since: Deadline) {
		val warmupTime: Deadline = since + 5.seconds
		val meltingTime: Deadline = since + 15.seconds
	}
}
