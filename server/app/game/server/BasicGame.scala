package game.server

import game.UID
import game.maps.GameMap
import game.protocol.ServerMessage
import game.server.actors.{Matchmaker, Watcher}
import scala.language.implicitConversions
import utils.ActorGroup

abstract class BasicGame(roster: Seq[GameTeam]) extends BasicActor("Game") {
	val teams: Map[UID, GameTeam] = roster.map(t => (t.info.uid, t)).toMap
	val players: Map[UID, GamePlayer] = roster.flatMap(_.players).map(p => (p.info.uid, p)).toMap

	val broadcast = ActorGroup(players.values.map(_.actor))
	val actors: Map[UID, ActorGroup] = {
		val ts = teams.view.map { case (uid, team) => (uid, ActorGroup(team.players.map(_.actor))) }
		val ps = players.view.map { case (uid, player) => (uid, ActorGroup(List(player.actor))) }
		(ts ++ ps).toMap
	}

	protected implicit def uidToActorGroup(uid: UID): ActorGroup = actors.get(uid) match {
		case Some(ag) => ag
		case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
	}

	final def receive: Receive = ({
		case Matchmaker.Start =>
			broadcast ! ServerMessage.GameStart
			start()
	}: Receive) orElse message

	def loadMap(map: GameMap): Unit = {

	}

	init()

	def init(): Unit
	def start(): Unit
	def message: Receive

	def terminate(): Unit = context.parent ! Watcher.Terminate
}
