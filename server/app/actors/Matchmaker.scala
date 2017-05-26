package actors

import actors.Matchmaker.{QueuedPlayer, WatcherOps}
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.pattern.ask
import akka.util.Timeout
import game.modes.twistingnether.TwistingNether
import game.modes.{GameBuilder, GamePlayer}
import game.protocol.ServerMessage
import game.shared.{PlayerInfo, UID}
import scala.annotation.tailrec
import scala.collection.immutable.TreeSet
import scala.concurrent.Future
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
			val builder = TwistingNether
			val watcher = Watcher.boundTo(sender)
			for (players <- fill(builder, watcher, Seq(GamePlayer(sender, player)), builder.playerSpots(1))) {
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
		// The queue is stable for 5 sec or more and the warmup time is expired
		val stableQueue = queue.head.warmupTime.isOverdue() && queueStabilityIndex >= 5
		// The queue is lengthy, no point in waiting for more players
		val lengthyQueue = queue.size > 20

		if (stableQueue || lengthyQueue) {
			val builder = GameBuilder.random
			val spots = builder.playerSpots(queue.size)
			if (queue.size >= spots || queue.head.meltingTime.isOverdue()) {
				val humans = pick(spots)
				val watcher = Watcher.boundTo(humans.map(_.actor))
				for (players <- fill(builder, watcher, humans, spots)) {
					build(builder, watcher, players)
				}
				maxQueueSize = queue.size
				queueStabilityIndex -= 1
				if (queue.nonEmpty) tick()
			}
		}
	}

	private def build(builder: GameBuilder, watcher: ActorRef, players: Seq[GamePlayer]): Unit = {
		val teams = builder.composeTeams(players)
		for (game <- watcher instantiate builder.gameProps(teams)) {
			val warmup = builder.warmup(players.size)
			val teamsInfo = teams.map(_.info)
			watcher ! Watcher.Ready
			for (player <- players) {
				player.actor ! Matchmaker.Bind(game)
				player.actor ! ServerMessage.GameFound(builder.mode, teamsInfo, player.info.uid, warmup)
			}
			system.scheduler.scheduleOnce(warmup.seconds, game, Matchmaker.Start)
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
	case object Start

	/** A queued player with some metadata */
	case class QueuedPlayer(actor: ActorRef, player: PlayerInfo, since: Deadline) {
		val warmupTime: Deadline = since + 5.seconds
		val meltingTime: Deadline = since + 15.seconds
	}

	implicit class WatcherOps(private val w: ActorRef) extends AnyVal {
		def instantiate(props: Props): Future[ActorRef] = {
			implicit val timeout = Timeout(60.seconds)
			(w ? Watcher.Instantiate(props)).mapTo[ActorRef]
		}
	}
}
