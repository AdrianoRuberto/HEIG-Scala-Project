package modes

import actors.{Matchmaker, Watcher}
import akka.actor.Actor
import game.UID

abstract class BasicGame(roster: Seq[GameTeam]) extends Actor {
	val teams: Map[UID, GameTeam] = roster.map(t => (t.info.uid, t)).toMap
	val players: Map[UID, GamePlayer] = roster.flatMap(_.players).map(p => (p.info.uid, p)).toMap

	final val receive: Receive = ({
		case Matchmaker.Start => start()
	}: Receive) orElse message

	init()

	def init(): Unit
	def start(): Unit
	def message: Receive

	def terminate(): Unit = context.parent ! Watcher.Terminate
}
