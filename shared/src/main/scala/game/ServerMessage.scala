package game

import boopickle.Default._

sealed trait ServerMessage

object ServerMessage {
	case class Error(error: String) extends ServerMessage

	sealed trait LobbyMessage extends ServerMessage
	case class QueueUpdate(count: Int) extends LobbyMessage
	case class GameFound(mode: GameMode, team: Seq[TeamInfo], me: UID, warmup: Int) extends LobbyMessage
	case object GameStart

	private implicit val UIDPicker = UID.pickler
	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
