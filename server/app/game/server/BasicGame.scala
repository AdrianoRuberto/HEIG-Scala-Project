package game.server

import akka.actor.ActorRef
import engine.geometry.{ColoredShape, Shape, Vector2D}
import game.UID
import game.doodads.Doodad
import game.maps.GameMap
import game.protocol.{ClientMessage, ServerMessage}
import game.server.actors.{Matchmaker, PlayerActor, Watcher}
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.skeleton.{AbstractSkeleton, ManagerEvent, RemoteManagerAgent, Skeleton}
import game.spells.effects.{SpellContext, SpellEffect}
import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.Random
import utils.ActorGroup

abstract class BasicGame(val roster: Seq[GameTeam]) extends BasicActor("Game") with BasicGameImplicits {
	import context._

	// --------------------------------
	// Basic data structures
	// --------------------------------

	/** A map from UID to GameTeam */
	val teamFromUID: Map[UID, GameTeam] = roster.map(t => (t.info.uid, t)).toMap

	/** A map from UID to GamePlayer */
	val playersFromUID: Map[UID, GamePlayer] = roster.flatMap(_.players).map(p => (p.info.uid, p)).toMap

	/** The reverse mapping from Actors to players */
	val uidFromActor: Map[ActorRef, UID] = playersFromUID.map { case (uid, player) => (player.actor, uid) }

	/**
	  * A map from UID to an suitable ActorGroup instance.
	  * - For team UID, the actor group is the combination of every players in the team;
	  * - For player UID, the actor group contain only a single actor: the player themselves
	  */
	val actorsFromUID: Map[UID, ActorGroup] = {
		val ts = teamFromUID.view.map { case (uid, team) => (uid, ActorGroup(team.players.map(_.actor))) }
		val ps = playersFromUID.view.map { case (uid, player) => (uid, ActorGroup(List(player.actor))) }
		(ts ++ ps).toMap
	}

	val teamForPlayer: Map[UID, UID] = {
		(for (team <- teamFromUID.values; player <- team.players) yield (player.info.uid, team.info.uid)).toMap
	}

	/** Color mapping of teams */
	var teamsColor: Map[UID, String] = Map.empty

	/** The sequence of every team's UID */
	val teams: Seq[UID] = roster.map(_.info.uid)

	/** A Map from team UID to its roster index */
	var teamsIndex: Map[UID, Int] = teams.zipWithIndex.toMap

	/** The sequence of every player's UID */
	val players: Seq[UID] = playersFromUID.keys.toSeq

	/** An actor group composed from every players in the game */
	val broadcast = ActorGroup(playersFromUID.values.map(_.actor))

	/** The map of every character skeletons */
	val skeletons: Map[UID, CharacterSkeleton] = playersFromUID.map { case (uid, player) =>
		val skeleton = createGlobalSkeleton(Skeleton.Character)
		skeleton.name.value = player.info.name
		broadcast ! ServerMessage.InstantiateCharacter(player.info.uid, skeleton.uid)
		(uid, skeleton)
	}

	/** The map of player UIDs to their network latency */
	var latencies: Map[UID, Double] = Map.empty.withDefaultValue(0.0)

	/** Players spells */
	var playerSpells: Map[UID, Array[Option[SpellSkeleton]]] = playersFromUID.keys.map { uid =>
		(uid, Array.fill[Option[SpellSkeleton]](4)(None))
	}.toMap

	/** Registered tickers */
	var doodads: Map[UID, Seq[UID]] = Map.empty

	/** Registered regions */
	var regions: Set[Region] = Set.empty

	/** Registered tickers */
	var tickers: Set[Ticker] = Set.empty

	/** Wall shapes */
	var shapes: Map[UID, Shape] = Map.empty

	/** Scheduled timers */
	val tasks: mutable.PriorityQueue[ScheduledTask] = mutable.PriorityQueue.empty

	context.system.scheduler.scheduleOnce(20.millis, self, BasicGame.Tick) // 50 Hz ticks
	override final def postStop(): Unit = tickers.foreach(_.remove())

	// --------------------------------
	// Implementation-defined behaviors
	// --------------------------------

	final def receive: Receive = {
		case Matchmaker.Start => broadcast ! ServerMessage.GameStart; start()
		case BasicGame.Tick => tick()
		case PlayerActor.UpdateLatency(latency) => latencies += (senderUID -> latency)
		case ClientMessage.Moving(x, y, duration, xs, ys) => playerMoving(senderUID, x, y, duration, xs, ys)
		case ClientMessage.Stopped(x, y, xs, ys) => playerStopped(senderUID, x, y, xs, ys)
		case ClientMessage.SpellCast(slot, point) => castSpell(senderUID, slot, point)
		case ClientMessage.SpellCancel(slot) => cancelSpell(senderUID, slot)
		case m => warn("Ignored unknown message:", m.toString)
	}

	def start(): Unit

	def hostile(a: UID, b: UID): Boolean = a != b && a.team != b.team
	def friendly(a: UID, b: UID): Boolean = a == b || a.team == b.team

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
		def followSelf(): Unit = for (uid <- playersFromUID.keys) uid ! ServerMessage.SetCameraFollow(uid)
		def detach(): Unit = broadcast ! ServerMessage.SetCameraFollow(UID.zero)
		def setSmoothing(smoothing: Boolean): Unit = broadcast ! ServerMessage.SetCameraSmoothing(smoothing)
		def setSpeed(pps: Double): Unit = broadcast ! ServerMessage.SetCameraSpeed(pps)
	}

	object engine {
		def enableInputs(): Unit = broadcast ! ServerMessage.EnableInputs
		def disableInputs(): Unit = broadcast ! ServerMessage.DisableInputs
	}

	def setTeamColors(colors: String*): Unit = {
		for ((uid, color) <- teams zip colors; team = teamFromUID(uid)) {
			teamsColor += (uid -> color)
			for (player <- team.players) skeletons(player.info.uid).color.value = color
		}
	}

	def setDefaultTeamColors(): Unit = setTeamColors("#77f", "#f55")

	def setDefaultCamera(): Unit = {
		camera.followSelf()
		camera.setSpeed(250)
	}

	// Skeleton
	def createSkeleton[T <: AbstractSkeleton](tpe: Skeleton[T], remotes: Seq[UID]): T = {
		tpe.instantiate(remotes.map { remote =>
			new RemoteManagerAgent {
				def send(event: ManagerEvent): Unit = {
					remote ! ServerMessage.SkeletonEvent(event)
				}
				def sendLatencyAware(f: (Double) => ManagerEvent): Unit = {
					remote ! ServerMessage.SkeletonEvent(f(remote.latency))
				}
			}
		})
	}

	def createSkeleton[T <: AbstractSkeleton](tpe: Skeleton[T], remote: UID): T = createSkeleton(tpe, Seq(remote))
	def createGlobalSkeleton[T <: AbstractSkeleton](tpe: Skeleton[T]): T = createSkeleton(tpe, players)

	// Doodads
	def createDoodad(doodad: Doodad, remotes: Seq[UID]): UID = {
		val uid = UID.next
		doodads += (uid -> remotes)
		remotes ! ServerMessage.CreateDoodad(uid, doodad)
		uid
	}

	def createDoodad(doodad: Doodad, remote: UID): UID = createDoodad(doodad, Seq(remote))
	def createGlobalDoodad(doodad: Doodad): UID = createDoodad(doodad, players)

	def destroyDoodad(uid: UID): Unit = {
		doodads.get(uid) match {
			case Some(group) =>
				group ! ServerMessage.DestroyDoodad(uid)
				doodads -= uid
			case None =>
				warn(s"Attempted to remove unknown doodad: $uid")
		}
	}

	// Regions
	def createRegion(shape: Shape,
	                 enters: UID => Unit = (_) => (),
	                 exits: UID => Unit = (_) => (),
	                 filter: UID => Boolean = (_) => true): Region = {
		val region = new Region(shape) {
			def playerEnters(uid: UID): Unit = enters(uid)
			def playerExits(uid: UID): Unit = exits(uid)
			def playerAccepted(uid: UID): Boolean = filter(uid)
			def remove(): Unit = unregisterRegion(this)
		}
		registerRegion(region)
		region
	}

	def registerRegion(region: Region): Unit = regions += region
	def unregisterRegion(region: Region): Unit = {
		regions -= region
		region.inside.foreach(region.playerExits)
		region.inside = Set.empty
	}

	// Ticker
	def createTicker(impl: Double => Unit): Ticker = {
		val ticker = new Ticker() {
			def tick(dt: Double): Unit = impl(dt)
			def remove(): Unit = unregisterTicker(this)
		}
		registerTicker(ticker)
		ticker
	}

	def registerTicker(ticker: Ticker): Unit = tickers += ticker
	def unregisterTicker(ticker: Ticker): Unit = tickers -= ticker

	// Timers
	def schedule(delay: Double)(action: => Unit): ScheduledTask = {
		val task = ScheduledTask(timestamp + delay, () => action)
		tasks += task
		task
	}

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

	def queryArea(area: Shape): Set[UID] = players.filter(player => area.contains(player.skeleton.position)).toSet

	/** Terminates the game, stopping every related actors and closing sockets */
	def terminate(): Unit = context.parent ! Watcher.Terminate

	// --------------------------------
	// Internal API
	// --------------------------------

	// Ticks
	private var timestamp = 0.0
	private var lastTick = Double.NaN

	private def tick(): Unit = {
		context.system.scheduler.scheduleOnce(20.millis, self, BasicGame.Tick)
		val now = System.nanoTime() / 1000000.0
		val dt = if (lastTick.isNaN) 0.0 else now - lastTick
		lastTick = now
		timestamp += dt
		while (tasks.nonEmpty && tasks.head.time <= timestamp) {
			val task = tasks.dequeue()
			if (!task.canceled) task.action()
		}
		for (region <- regions) {
			val inside = queryArea(region.shape).filter(region.playerAccepted)
			for (player <- inside diff region.inside) region.playerEnters(player)
			for (player <- region.inside diff inside) region.playerExits(player)
			region.inside = inside
		}
		for (ticker <- tickers) ticker.tick(dt)
	}

	def time: Double = timestamp

	/** Computes players spawn around a point for a given team */
	def spawnPlayers(center: Vector2D, players: Seq[GamePlayer]): Unit = {
		val alpha = Math.PI * 2 / players.size
		val radius = if (players.size == 1) 0 else 30 / Math.sin(alpha / 2)
		val start = Random.nextDouble() * Math.PI * 2
		for ((player, i) <- players.zipWithIndex; skeleton = player.info.uid.skeleton) {
			val beta = alpha * i + start
			skeleton.x.value = center.x + Math.sin(beta) * radius
			skeleton.y.value = center.y + Math.cos(beta) * radius
		}
	}

	def playerMoving(uid: UID, x: Double, y: Double, duration: Double, xs: Int, ys: Int): Unit = {
		val skeleton = uid.skeleton
		val latency = uid.latency
		skeleton.moving.value = true
		skeleton.x.commit(xs).interpolate(x, duration - latency)
		skeleton.y.commit(ys).interpolate(y, duration - latency)
	}

	def playerStopped(uid: UID, x: Double, y: Double, xs: Int, ys: Int): Unit = {
		val skeleton = uid.skeleton
		skeleton.moving.value = false
		skeleton.x.commit(xs).interpolate(x, 200)
		skeleton.y.commit(ys).interpolate(y, 200)
	}

	def castSpell(player: UID, slot: Int, point: Vector2D): Unit = player.spells(slot) match {
		case Some(skeleton) =>
			SpellEffect.forSpell(skeleton.spell.value).cast(SpellContext(this, skeleton, player, point))
		case None =>
			warn(s"Player `${player.skeleton.name.value}` attempted to cast spell from empty slot")
	}

	def cancelSpell(player: UID, slot: Int): Unit = player.spells(slot) match {
		case Some(skeleton) =>
			SpellEffect.forSpell(skeleton.spell.value).cancel(SpellContext(this, skeleton, player, Vector2D.zero))
		case None =>
			warn(s"Player `${player.skeleton.name.value}` attempted to cancel spell from empty slot")
	}

	/** Retrieves the sender's UID */
	@inline private def senderUID: UID = uidFromActor(sender())
}

object BasicGame {
	case object Tick
}
