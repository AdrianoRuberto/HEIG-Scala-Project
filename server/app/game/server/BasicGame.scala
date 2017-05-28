package game.server

import game.protocol.ServerMessage
import game.server.actors.{Matchmaker, Watcher}
import game.shared.UID
import utils.ActorGroup

abstract class BasicGame(roster: Seq[GameTeam]) extends BasicActor("Game") {
	val teams: Map[UID, GameTeam] = roster.map(t => (t.info.uid, t)).toMap
	val players: Map[UID, GamePlayer] = roster.flatMap(_.players).map(p => (p.info.uid, p)).toMap

	val broadcast = ActorGroup(players.values.map(_.actor))

	final def receive: Receive = ({
		case Matchmaker.Start =>
			broadcast ! ServerMessage.GameStart
			start()
	}: Receive) orElse message

	init()

	def init(): Unit
	def start(): Unit
	def message: Receive

	def terminate(): Unit = context.parent ! Watcher.Terminate
}
