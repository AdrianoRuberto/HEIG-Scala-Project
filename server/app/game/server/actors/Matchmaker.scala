package game.server.actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.pattern.ask
import akka.util.Timeout
import game.protocol.ServerMessage
import game.server.actors.Matchmaker.{QueuedPlayer, WatcherOps}
import game.server.modes.twistingnether.TwistingNetherBuilder
import game.server.{GameBuilder, GamePlayer}
import game.{PlayerInfo, UID}
import scala.annotation.tailrec
import scala.collection.immutable.TreeSet
import scala.concurrent.Future
import scala.concurrent.duration._
import utils.NameGenerator

class Matchmaker extends Actor {
	import Matchmaker.dummyTimeout
	import context._

	private var queue = TreeSet.empty[QueuedPlayer](Ordering.by(_.since))
	private var players = Map.empty[ActorRef, QueuedPlayer]

	private var ticking = false
	private var lastQueueSize = 0
	private var maxQueueSize = 0
	private var queueStabilityIndex = 0

	def receive: Receive = {
		case Matchmaker.Register(player, true) =>
			val builder = TwistingNetherBuilder
			val watcher = Watcher.boundTo(sender)
			for (players <- fill(builder, watcher, Seq(GamePlayer(sender, player)), builder.playerSpots(1, 1.hour))) {
				build(builder, watcher, players)
			}

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
		// The queue is stable for 5 sec or more
		val stableQueue = queueStabilityIndex >= 5
		// The queue is lengthy, no point in waiting for more players
		val lengthyQueue = queue.size >= 10

		if (stableQueue || lengthyQueue) {
			val builder = GameBuilder.random
			val spots = builder.playerSpots(queue.size, Deadline.now - queue.head.since)
			if (queue.size >= spots && queue.view.take(spots).forall(qp => Deadline.now - qp.since > 5.seconds)) {
				val humans = pick(spots)
				val watcher = Watcher.boundTo(humans.map(_.actor))
				for (players <- fill(builder, watcher, humans, spots)) {
					build(builder, watcher, players)
				}
				maxQueueSize = queue.size
				queueStabilityIndex = 0
				if (queue.nonEmpty) tick()
			}
		}
	}

	private def build(builder: GameBuilder, watcher: ActorRef, players: Seq[GamePlayer]): Unit = {
		val teams = builder.composeTeams(players)
		val teamsInfo = teams.map(_.info)
		val warmup = builder.warmup(players.size)
		val warmupDeadline = warmup.seconds.fromNow
		val ready = Future.sequence(for (player <- players) yield {
			player.actor ? ServerMessage.GameFound(builder.mode, teamsInfo, player.info.uid, warmup)
		})
		for (_ <- ready; game <- watcher instantiate builder.gameProps(teams)) {
			watcher ! Watcher.Ready
			for (player <- players) player.actor ! Matchmaker.Bind(game)
			system.scheduler.scheduleOnce(warmupDeadline.timeLeft, game, Matchmaker.Start)
		}
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

	private def fill(builder: GameBuilder, watcher: ActorRef, humans: Seq[GamePlayer], spots: Int): Future[Seq[GamePlayer]] = {
		for (bots <- buildBots(builder, watcher, spots - humans.size)) yield humans ++ bots
	}

	private def buildBots(builder: GameBuilder, watcher: ActorRef, spots: Int): Future[Seq[GamePlayer]] = {
		Future.sequence(Seq.fill(spots) {
			val name = NameGenerator.generate
			for (bot <- watcher instantiate builder.botProps(name)) yield {
				GamePlayer(bot, PlayerInfo(UID.next, name, bot = true))
			}
		})
	}
}

object Matchmaker {
	case class Register(player: PlayerInfo, fast: Boolean)
	case object Tick
	case class Bind(game: ActorRef)
	case object Ready
	case object Start

	implicit val dummyTimeout: akka.util.Timeout = Timeout(10.seconds)

	/** A queued player with some metadata */
	case class QueuedPlayer(actor: ActorRef, player: PlayerInfo, since: Deadline)

	implicit class WatcherOps(private val w: ActorRef) extends AnyVal {
		def instantiate(props: Props): Future[ActorRef] = {
			(w ? Watcher.Instantiate(props)).mapTo[ActorRef]
		}
	}
}
