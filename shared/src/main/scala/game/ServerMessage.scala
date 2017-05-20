package game

import boopickle.Default._

sealed trait ServerMessage

object ServerMessage {
	case class Error(error: String) extends ServerMessage
	case class JsonError(error: String) extends ServerMessage
	case object ServerError extends ServerMessage
	case object GameEnd extends ServerMessage

	sealed trait LobbyMessage extends ServerMessage
	case class QueueUpdate(count: Int) extends LobbyMessage
	case class GameFound(mode: GameMode, team: Seq[TeamInfo], me: UID, warmup: Int) extends LobbyMessage
	case object GameStart extends LobbyMessage

	private implicit val UIDPicker = UID.pickler
	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
