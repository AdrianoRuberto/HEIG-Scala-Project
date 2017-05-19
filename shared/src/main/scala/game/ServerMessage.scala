package game

import boopickle.Default._

sealed trait ServerMessage

object ServerMessage {
	case class Error(error: String) extends ServerMessage

	sealed trait LobbyMessage extends ServerMessage
	case class QueueUpdate(count: Int) extends LobbyMessage
	case class GameFound(players: Seq[Team], me: Long) extends LobbyMessage

	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
