package game.server

import akka.actor.ActorRef
import engine.geometry.{Shape, Vector2D}
import game.UID
import game.doodads.Doodad
import game.protocol.{ClientMessage, ServerMessage}
import game.server.BasicGame.UIDGroup
import game.server.actors.{Matchmaker, PlayerActor, Watcher}
import game.skeleton.core.{CharacterSkeleton, SpellSkeleton}
import game.skeleton.{AbstractSkeleton, ManagerEvent, RemoteManagerAgent, Skeleton}
import game.spells.effects.base.{SpellContext, SpellEffect}
import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.implicitConversions
import scala.util.Random
import utils.Color

/**
  * BasicGame is the base class for every game mode implementation.
  *
  * @param roster the roster of players for this game
  */
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

	val teamForPlayer: Map[UID, UID] = {
		(for (team <- teamFromUID.values; player <- team.players) yield (player.info.uid, team.info.uid)).toMap
	}

	/** Color mapping of teams */
	var teamsColor: Map[UID, Color] = Map.empty

	/** The sequence of every team's UID */
	val teams: Seq[UID] = roster.map(_.info.uid)

	/** A Map from team UID to its roster index */
	var teamsIndex: Map[UID, Int] = teams.zipWithIndex.toMap

	/** The sequence of every player's UID */
	val players: Seq[UID] = playersFromUID.keys.toSeq

	/** The map of every character skeletons */
	val skeletons: Map[UID, CharacterSkeleton] = playersFromUID.map { case (uid, player) =>
		val skeleton = createSkeleton(Skeleton.Character)
		skeleton.name.value = player.info.name
		players ! ServerMessage.InstantiateCharacter(player.info.uid, skeleton.uid)
		(uid, skeleton)
	}

	/** The map of player UIDs to their network latency */
	var latencies: Map[UID, Double] = Map.empty.withDefaultValue(0.0)

	/** Players spells */
	var playerSpells: Map[UID, Array[Option[SpellSkeleton]]] = playersFromUID.keys.map { uid =>
		(uid, Array.fill[Option[SpellSkeleton]](4)(None))
	}.toMap

	/** Registered regions */
	var regions: Set[Region] = Set.empty

	/** Registered tickers */
	var tickers: Set[Ticker] = Set.empty

	/** Scheduled timers */
	val tasks: mutable.PriorityQueue[ScheduledTask] = mutable.PriorityQueue.empty

	/** Wall shapes */
	var walls: Set[Shape] = Set.empty

	context.system.scheduler.scheduleOnce(20.millis, self, BasicGame.Tick) // 50 Hz ticks
	override final def postStop(): Unit = tickers.foreach(_.remove())

	// --------------------------------
	// Implementation-defined behaviors
	// --------------------------------

	final def receive: Receive = {
		case Matchmaker.Start => players ! ServerMessage.GameStart; start()
		case BasicGame.Tick => tick()
		case PlayerActor.UpdateLatency(latency) => latencies += (senderUID -> latency)
		case ClientMessage.Moving(x, y, duration, xs, ys) => playerMoving(senderUID, x, y, duration, xs, ys)
		case ClientMessage.Stopped(x, y, xs, ys) => playerStopped(senderUID, x, y, xs, ys)
		case ClientMessage.SpellCast(slot, point) => castSpell(senderUID, slot, point)
		case ClientMessage.SpellCancel(slot) => cancelSpell(senderUID, slot)
		case m => warn("Ignored unknown message:", m.toString)
	}

	/** Called when the game starts */
	def start(): Unit = ()

	/**
	  * Check the hostility status between two players.
	  * By default, two players are hostile if not in the same team.
	  */
	def hostile(a: UID, b: UID): Boolean = a != b && a.team != b.team

	/**
	  * Cheks the friendly status between two players.
	  * By default, two players are friendly if they are in the same team.
	  */
	def friendly(a: UID, b: UID): Boolean = a == b || a.team == b.team

	// --------------------------------
	// Common Game API
	// --------------------------------

	/** Loads the given map, initializes geometry and spawns players */
	def loadMap(map: GameMap): Unit = {
		for (shape <- map.geometry) createWall(shape, map.color)
		if (map.spawns.nonEmpty) {
			require(map.spawns.size == roster.size, "Map must have as many spawns as there are teams in the game")
			for ((spawn, team) <- map.spawns zip roster) spawnPlayers(spawn, team.players)
		}
	}

	/** Defines colors for teams */
	def setTeamColors(colors: Color*): Unit = {
		for ((uid, color) <- teams zip colors; team = teamFromUID(uid)) {
			teamsColor += (uid -> color)
			for (player <- team.players) skeletons(player.info.uid).color.value = color
		}
	}

	/** Sets default team colors */
	def setDefaultTeamColors(): Unit = setTeamColors(Color("#77f"), Color("#f55"))

	/** Sets default camera behavior */
	def setDefaultCamera(): Unit = {
		players.camera.followSelf()
		players.camera.setSpeed(250)
	}

	/**
	  * Constructs a new Skeleton object
	  *
	  * @param tpe     the type of skeleton to create
	  * @param remotes the list of players UIDs who should see the newly created doodad
	  * @tparam T the type of skeleton created
	  */
	def createSkeleton[T <: AbstractSkeleton](tpe: Skeleton[T], remotes: UIDGroup = players): T = {
		tpe.instantiate(remotes.members.map { remote =>
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

	/**
	  * Constructs a static Doodad.
	  *
	  * @param doodad  the doodad defining case class instance
	  * @param remotes the list of players UIDs who should see the newly created doodad
	  */
	def createDoodad(doodad: Doodad, remotes: UIDGroup = players): DoodadInstance.Static = {
		val uid = UID.next
		remotes.members ! ServerMessage.CreateDoodad(uid, doodad)
		new DoodadInstance.Static(uid) {
			def remove(): Unit = remotes.members ! ServerMessage.RemoveDoodad(uid)
		}
	}

	/**
	  * Constructs a new dynamic Doodad with its associated Skeleton.
	  *
	  * The doodad is given as its defining case class companion object. The
	  * companion must extends the type `UID => Doodad`, which should be the case
	  * if the associated case class has a single argument of type `UID` and no
	  * companion object is explicitly defined.
	  *
	  * @param doodad   the doodad to create
	  * @param skeleton the skeleton associated with the Doodad
	  * @param remotes  the list of players UIDs who should see the newly created doodad
	  * @tparam T the type of skeleton instance
	  */
	def createDynamicDoodad[T <: AbstractSkeleton](doodad: UID => Doodad,
	                                               skeleton: Skeleton[T],
	                                               remotes: UIDGroup = players): DoodadInstance.Dynamic[T] = {
		val skeletonInstance = createSkeleton(skeleton, remotes)
		createDoodad(doodad(skeletonInstance.uid), remotes).withSkeleton(skeletonInstance)
	}

	/**
	  * Creates a new region on the game world and tracks player movement in and
	  * out of it. The region will call the supplied `enters` and `exits` functions
	  * whenever a player enters or leaves the region.
	  *
	  * If the `filter` predicate is given, only players for which the filters
	  * returns true will be tracked. If no filter is given, every players will
	  * be tracked.
	  *
	  * @param shape  the region shape
	  * @param enters a function to call whenever a player enters the region
	  * @param exits  a function to call whenever a player leaves the region
	  * @param filter a predicated used to ignore some players from the region
	  * @return a [[Region]] object that can be used to query the current state of
	  *         the region or unregister it from the game engine.
	  */
	def createRegion(shape: Shape,
	                 enters: UID => Unit = (_) => (),
	                 exits: UID => Unit = (_) => (),
	                 filter: UID => Boolean = (_) => true): Region = {
		val region = new Region(shape) {
			def playerEnters(uid: UID): Unit = enters(uid)
			def playerExits(uid: UID): Unit = exits(uid)
			def playerAccepted(uid: UID): Boolean = filter(uid)
			def remove(): Unit = {
				regions -= this
				inside.foreach(playerExits)
			}
		}
		regions += region
		region
	}

	def registerRegion(region: Region): Unit = regions += region

	/**
	  * Creates a new Ticker object that can be used to executes custom code
	  * periodically during the game. The given function will be called every
	  * game tick with the time delta (as milliseconds) since last tick as a
	  * parameter.
	  *
	  * @param impl the tick action to executes
	  * @return a [[Ticker]] object that can be used to remove the ticker
	  */
	def createTicker(impl: Double => Unit): Ticker = {
		val ticker = new Ticker() {
			def tick(dt: Double): Unit = impl(dt)
			def remove(): Unit = tickers -= this
		}
		tickers += ticker
		ticker
	}

	/**
	  * Schedules an action to be executed in `delay` milliseconds from now.
	  *
	  * Please note that actions are executed as part of the game tick loop,
	  * their resolution is thus limited by the game tick frequency, which is
	  * equals to 50 Hz, resulting in a minimum scheduler resolution of 20 ms.
	  *
	  * The scheduler will never execute a task before its scheduled time.
	  * As a result, it is possible for actual execution to be delayed by
	  * an additional 20 ms if due time occurs right after a game tick.
	  *
	  * @param delay  the time in ms from now at which the action should be executed
	  * @param action the action to execute
	  * @return a [[ScheduledTask]] object that can be used to cancel the task
	  */
	def schedule(delay: Double)(action: => Unit): ScheduledTask = {
		val task = ScheduledTask(time + delay, () => action)
		tasks += task
		task
	}

	/** Creates a new wall, along with its associated visual doodad */
	def createWall(shape: Shape, color: Color): DoodadInstance.Static = {
		walls += shape
		val doodad = createDoodad(Doodad.Area.Wall(shape, color))
		val uid = doodad.uid
		players ! ServerMessage.CreateWall(uid, shape)
		new DoodadInstance.Static(uid) {
			/** Removes this doodad */
			def remove(): Unit = {
				walls -= shape
				players ! ServerMessage.RemoveWall(uid)
				doodad.remove()
			}
		}
	}

	/** Query a given area of the game world for intersecting players */
	def queryArea(area: Shape): Set[UID] = players.filter(player => area.contains(player.skeleton.position)).toSet

	/** Terminates the game, stopping every related actors and closing sockets */
	def terminate(): Unit = context.parent ! Watcher.Terminate

	// --------------------------------
	// Internal API
	// --------------------------------

	/** Game start time */
	private val startTime = System.nanoTime() / 1000000.0

	/** Timestamp of the last game tick */
	private var lastTick = startTime

	/** Current game time */
	private var currentTime: Double = 0.0

	/** Performs game tick */
	private def tick(): Unit = {
		// Schedule next tick
		context.system.scheduler.scheduleOnce(20.millis, self, BasicGame.Tick)

		// Update current time and compute delta since last tick
		currentTime = (System.nanoTime() / 1000000.0) - startTime
		val dt = currentTime - lastTick
		lastTick = currentTime

		// Execute scheduled tasks
		while (tasks.nonEmpty && tasks.head.time <= currentTime) {
			val task = tasks.dequeue()
			if (!task.canceled) task.action()
		}

		// Updates regions status
		for (region <- regions) {
			val inside = queryArea(region.shape).filter(region.playerAccepted)
			for (player <- inside diff region.inside) region.playerEnters(player)
			for (player <- region.inside diff inside) region.playerExits(player)
			region.inside = inside
		}

		// Invoke tickers
		for (ticker <- tickers) ticker.tick(dt)
	}

	/** Current game time as milliseconds since game initialization */
	def time: Double = currentTime

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

	/** The player started moving */
	def playerMoving(uid: UID, x: Double, y: Double, duration: Double, xs: Int, ys: Int): Unit = {
		val skeleton = uid.skeleton
		val latency = uid.latency
		skeleton.moving.value = true
		skeleton.x.commit(xs).interpolate(x, duration - latency)
		skeleton.y.commit(ys).interpolate(y, duration - latency)
	}

	/** The player stopped moving */
	def playerStopped(uid: UID, x: Double, y: Double, xs: Int, ys: Int): Unit = {
		val skeleton = uid.skeleton
		skeleton.moving.value = false
		skeleton.x.commit(xs).interpolate(x, 200)
		skeleton.y.commit(ys).interpolate(y, 200)
	}

	/** Casts a spell, called when the player presses the key for the corresponding spell */
	def castSpell(player: UID, slot: Int, point: Vector2D): Unit = player.spells(slot) match {
		case Some(skeleton) =>
			SpellEffect.forSpell(skeleton.spell.value).cast(SpellContext(this, skeleton, player, point))
		case None =>
			warn(s"Player `${player.skeleton.name.value}` attempted to cast spell from empty slot")
	}

	/** Cancels an activated spell, called when the player releases the key for the corresponding spell */
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
	/** Object used as Tick message for the game main loop */
	case object Tick

	/** Wrappers around an iterable of UID used as a trigger for implicits conversions */
	class UIDGroup (val members: Iterable[UID]) extends AnyVal

	/** Implicitly converts from a single UID to a UIDGroup */
	@inline implicit def UIDGroupFromSingle(uid: UID): UIDGroup = new UIDGroup(Seq(uid))

	/** Implicitly converts from an Iterable collection of UID to a UIDGroup */
	@inline implicit def UIDGroupFromIterable(uids: Iterable[UID]): UIDGroup = new UIDGroup(uids)
}
