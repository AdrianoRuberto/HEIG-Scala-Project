package game.server

import akka.actor.ActorRef
import engine.geometry.{ColoredShape, Shape, Vector}
import game.UID
import game.maps.GameMap
import game.protocol.enums.SkeletonType
import game.protocol.{ClientMessage, ServerMessage}
import game.server.actors.{Matchmaker, PlayerActor, Watcher}
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.skeleton.{AbstractSkeleton, ManagerEvent, RemoteManager}
import game.spells.effects.{SpellContext, SpellEffect}
import scala.concurrent.duration._
import scala.util.Random
import utils.ActorGroup

abstract class BasicGame(roster: Seq[GameTeam]) extends BasicActor("Game") with BasicGameImplicits {
	import context._

	// --------------------------------
	// Basic data structures
	// --------------------------------

	/** A map from UID to GameTeam */
	val teams: Map[UID, GameTeam] = roster.map(t => (t.info.uid, t)).toMap

	/** A map from UID to GamePlayer */
	val players: Map[UID, GamePlayer] = roster.flatMap(_.players).map(p => (p.info.uid, p)).toMap

	/** The reverse mapping from Actors to players */
	val actorsUID: Map[ActorRef, UID] = players.map { case (uid, player) => (player.actor, uid) }

	/** An actor group composed from every players in the game */
	val broadcast = ActorGroup(players.values.map(_.actor))

	/**
	  * A map from UID to an suitable ActorGroup instance.
	  * - For team UID, the actor group is the combination of every players in the team;
	  * - For player UID, the actor group contain only a single actor: the player themselves
	  */
	val actors: Map[UID, ActorGroup] = {
		val ts = teams.view.map { case (uid, team) => (uid, ActorGroup(team.players.map(_.actor))) }
		val ps = players.view.map { case (uid, player) => (uid, ActorGroup(List(player.actor))) }
		(ts ++ ps).toMap
	}

	/** The map of every character skeletons */
	val skeletons: Map[UID, CharacterSkeleton] = players.map { case (uid, player) =>
		val skeleton = createGlobalSkeleton(SkeletonType.Character)
		skeleton.name.value = player.info.name
		broadcast ! ServerMessage.InstantiateCharacter(player.info.uid, skeleton.uid)
		(uid, skeleton)
	}

	/** The map of player UIDs to their network latency */
	var latencies: Map[UID, Double] = Map.empty.withDefaultValue(0.0)

	/** Players spells */
	var spells: Map[UID, Array[Option[SpellSkeleton]]] = players.keys.map { uid =>
		(uid, Array.fill[Option[SpellSkeleton]](4)(None))
	}.toMap

	/** Registered tickers */
	var tickers: Set[Ticker] = Set.empty

	/** Wall shapes */
	var shapes: Map[UID, Shape] = Map.empty

	init()
	context.system.scheduler.scheduleOnce(20.millis, self, BasicGame.Tick) // 50 Hz ticks
	override final def postStop(): Unit = tickers.foreach(_.unregister())

	// --------------------------------
	// Implementation-defined behaviors
	// --------------------------------

	final def receive: Receive = ({
		case Matchmaker.Start =>
			broadcast ! ServerMessage.GameStart
			start()
		case BasicGame.Tick => tickImpl()
		case PlayerActor.UpdateLatency(latency) => latencies += (senderUID -> latency)
		case ClientMessage.Moving(x, y, xs, ys) => playerMoving(senderUID, x, y, xs, ys)
		case ClientMessage.Stopped(x, y, xs, ys) => playerStopped(senderUID, x, y, xs, ys)
		case ClientMessage.SpellCast(slot, point) => castSpell(senderUID, slot, point)
		case ClientMessage.SpellCancel(slot) => cancelSpell(senderUID, slot)
	}: Receive) orElse message orElse { case m => warn("Ignored unknown message:", m.toString) }

	def init(): Unit
	def start(): Unit
	def message: Receive
	def tick(dt: Double): Unit

	/** Terminates the game, stopping every related actors and closing sockets */
	def terminate(): Unit = context.parent ! Watcher.Terminate

	// --------------------------------
	// Common Game API
	// --------------------------------

	/** Loads the given map, initializing geometries and spawning players */
	def loadMap(map: GameMap): Unit = {
		if (map.spawns.nonEmpty) {
			require(map.spawns.size == roster.size, "Map must have as many spawns as there are teams in the game")
			for ((spawn, team) <- map.spawns zip roster) spawnPlayers(spawn, team.players)
		}

		for (shape <- map.geometry) addShape(shape)
	}

	/** Camera manipulation utilities */
	object camera {
		def move(x: Double, y: Double): Unit = broadcast ! ServerMessage.SetCameraLocation(x, y)
		def follow(uid: UID): Unit = broadcast ! ServerMessage.SetCameraFollow(uid)
		def followSelf(): Unit = for (uid <- players.keys) uid ! ServerMessage.SetCameraFollow(uid)
		def detach(): Unit = broadcast ! ServerMessage.SetCameraFollow(UID.zero)
		def setSmoothing(smoothing: Boolean): Unit = broadcast ! ServerMessage.SetCameraSmoothing(smoothing)
		def setSpeed(pps: Double): Unit = broadcast ! ServerMessage.SetCameraSpeed(pps)
	}

	def setTeamColors(colors: String*): Unit = {
		for ((team, color) <- teams.values zip colors; player <- team.players) {
			skeletons(player.info.uid).color.value = color
		}
	}

	def setDefaultTeamColors(): Unit = setTeamColors("#77f", "#f55")

	// Skeleton
	def createSkeleton[T <: AbstractSkeleton](tpe: SkeletonType[T], remotes: Seq[UID]): T = {
		tpe.instantiate(remotes.map { remote =>
			new RemoteManager {
				def send(event: ManagerEvent): Unit = {
					remote ! ServerMessage.SkeletonEvent(event)
				}
				def sendLatencyAware(f: (Double) => ManagerEvent): Unit = {
					remote ! ServerMessage.SkeletonEvent(f(remote.latency))
				}
			}
		})
	}

	def createSkeleton[T <: AbstractSkeleton](tpe: SkeletonType[T], remote: UID): T = createSkeleton(tpe, Seq(remote))
	def createGlobalSkeleton[T <: AbstractSkeleton](tpe: SkeletonType[T]): T = createSkeleton(tpe, players.keys.toSeq)

	// Ticker
	def createTicker(tick: Double => Unit): Ticker = {
		val ticker = new Ticker() {
			def tick(dt: Double): Unit = tick(dt)
			def unregister(): Unit = unregisterTicker(this)
		}
		registerTicker(ticker)
		ticker
	}

	def registerTicker(ticker: Ticker): Unit = tickers += ticker
	def unregisterTicker(ticker: Ticker): Unit = tickers -= ticker

	// Shapes
	def addShape(coloredShape: ColoredShape): UID = {
		val uid = UID.next
		shapes += (uid -> coloredShape)
		broadcast ! ServerMessage.DrawShape(uid, coloredShape)
		uid
	}

	def deleteShape(uid: UID): Unit = {
		shapes -= uid
		broadcast ! ServerMessage.EraseShape(uid)
	}

	// --------------------------------
	// Internal API
	// --------------------------------

	// Ticks
	private var lastTick = Double.NaN
	private def tickImpl(): Unit = {
		context.system.scheduler.scheduleOnce(20.millis, self, BasicGame.Tick)
		val now = System.nanoTime() / 1000000.0
		val dt = if (lastTick.isNaN) 0.0 else now - lastTick
		lastTick = now
		tick(dt)
		for (ticker <- tickers) ticker.tick(dt)
	}

	/** Computes players spawn around a point for a given team */
	def spawnPlayers(center: Vector, players: Seq[GamePlayer]): Unit = {
		val alpha = Math.PI * 2 / players.size
		val radius = if (players.size == 1) 0 else 30 / Math.sin(alpha / 2)
		val start = Random.nextDouble() * Math.PI * 2
		for ((player, i) <- players.zipWithIndex; skeleton = player.info.uid.skeleton) {
			val beta = alpha * i + start
			skeleton.x.value = center.x + Math.sin(beta) * radius
			skeleton.y.value = center.y + Math.cos(beta) * radius
		}
	}

	def playerMoving(uid: UID, x: Double, y: Double, xs: Int, ys: Int): Unit = {
		val skeleton = uid.skeleton
		val latency = uid.latency
		skeleton.moving.value = true
		skeleton.x.commit(xs).interpolate(x, 2000 - latency)
		skeleton.y.commit(ys).interpolate(y, 2000 - latency)
	}

	def playerStopped(uid: UID, x: Double, y: Double, xs: Int, ys: Int): Unit = {
		val skeleton = uid.skeleton
		skeleton.moving.value = false
		skeleton.x.commit(xs).interpolate(x, 200)
		skeleton.y.commit(ys).interpolate(y, 200)
	}

	def castSpell(player: UID, slot: Int, point: Vector): Unit = player.spells(slot) match {
		case Some(skeleton) =>
			SpellEffect.forSpell(skeleton.spell.value).cast(SpellContext(this, skeleton, player, point))
		case None =>
			warn(s"Player `${player.skeleton.name.value}` attempted to cast spell from empty slot")
	}

	def cancelSpell(player: UID, slot: Int): Unit = player.spells(slot) match {
		case Some(skeleton) =>
			SpellEffect.forSpell(skeleton.spell.value).cancel(SpellContext(this, skeleton, player, Vector.zero))
		case None =>
			warn(s"Player `${player.skeleton.name.value}` attempted to cancel spell from empty slot")
	}

	/** Retrieves the sender's UID */
	@inline private def senderUID: UID = actorsUID(sender())
}

object BasicGame {
	case object Tick
}
