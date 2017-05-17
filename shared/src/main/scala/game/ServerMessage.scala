package game

import boopickle.Default._

sealed trait ServerMessage

object ServerMessage {
	case class Error(error: String) extends ServerMessage
	case class GameFound(players: Seq[Team]) extends ServerMessage

	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
