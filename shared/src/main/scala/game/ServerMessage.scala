package game

import boopickle.Default._

sealed trait ServerMessage

object ServerMessage {
	case class GameFound(players: Seq[Team]) extends ServerMessage

	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
