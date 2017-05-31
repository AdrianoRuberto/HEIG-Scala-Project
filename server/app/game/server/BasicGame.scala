package game.server

import akka.actor.ActorRef
import engine.geometry.Point
import game.UID
import game.maps.GameMap
import game.protocol.{ClientMessage, ServerMessage}
import game.server.actors.{Matchmaker, PlayerActor, Watcher}
import game.skeleton.concrete.CharacterSkeleton
import scala.util.Random
import utils.ActorGroup

abstract class BasicGame(roster: Seq[GameTeam]) extends BasicActor("Game") with BasicGameImplicits {
	// --------------------------------
	// Basic data structures
	// --------------------------------

	/** A map from UID to GameTeam */
	val teams: Map[UID, GameTeam] = roster.map(t => (t.info.uid, t)).toMap

	/** A map from UID to GamePlayer */
	val players: Map[UID, GamePlayer] = roster.flatMap(_.players).map(p => (p.info.uid, p)).toMap

	/** The reverse mapping from Actors to players */
	val uids: Map[ActorRef, UID] = players.map { case (uid, player) => (player.actor, uid) }

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
		val skeleton = new CharacterSkeleton
		skeleton.name.value = player.info.name
		broadcast ! ServerMessage.InstantiateCharacter(player.info.uid, skeleton.uid)
		(uid, skeleton)
	}

	/** The map of player UIDs to their network latency */
	var latencies: Map[UID, Double] = Map.empty.withDefaultValue(0.0)

	init()

	// --------------------------------
	// Implementation-defined behaviors
	// --------------------------------

	final def receive: Receive = ({
		case Matchmaker.Start =>
			broadcast ! ServerMessage.GameStart
			start()
		case PlayerActor.UpdateLatency(latency) => latencies += (senderUID -> latency)
		case ClientMessage.Moving(angle) => playerMoving(senderUID, angle)
		case ClientMessage.Stopped(x, y) => playerStopped(senderUID, x, y)
	}: Receive) orElse message orElse {
		case m => warn("Ignored unknown message:", m.toString)
	}

	def init(): Unit
	def start(): Unit
	def message: Receive

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

	def setDefaultTeamColors(): Unit = setTeamColors("#5a5", "#f55")

	// --------------------------------
	// Internal API, override if required
	// --------------------------------

	/** Computes players spawn around a point for a given team */
	def spawnPlayers(center: Point, players: Seq[GamePlayer]): Unit = {
		val alpha = Math.PI * 2 / players.size
		val radius = if (players.size == 1) 0 else 30 / Math.sin(alpha / 2)
		val start = Random.nextDouble() * Math.PI * 2
		for ((player, i) <- players.zipWithIndex; skeleton = skeletons(player.info.uid)) {
			val beta = alpha * i + start
			skeleton.x.value = center.x + Math.sin(beta) * radius
			skeleton.y.value = center.y + Math.cos(beta) * radius
		}
	}

	def playerMoving(uid: UID, angle: Double): Unit = {
		val skeleton = uid.skeleton
		skeleton.moving.value = true

		val speed = skeleton.speed.value
		val tx = skeleton.x.current + Math.cos(angle) * speed
		val ty = skeleton.y.current + Math.sin(angle) * speed

		val latency = uid.latency
		skeleton.x.interpolate(tx, 1000 - latency)
		skeleton.y.interpolate(ty, 1000 - latency)
	}

	def playerStopped(uid: UID, x: Double, y: Double): Unit = {
		val skeleton = uid.skeleton
		skeleton.moving.value = false
		skeleton.x.interpolate(x, 50)
		skeleton.y.interpolate(y, 50)
	}

	/** Retrieves the sender's UID */
	def senderUID: UID = uids(sender())
}
