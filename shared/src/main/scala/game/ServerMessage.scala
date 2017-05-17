package game

import boopickle.Default._

/**
  * Created by Adriano on 17.05.2017.
  */
sealed trait ServerMessage

case class GameFound(players: Seq[Team]) extends ServerMessage

object ServerMessage {
	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}