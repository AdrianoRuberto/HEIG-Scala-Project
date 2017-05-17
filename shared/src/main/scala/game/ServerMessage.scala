package game

import boopickle.DefaultBasic.{Pickler, PicklerGenerator}

/**
  * Created by Adriano on 17.05.2017.
  */
sealed trait ServerMessage

object ServerMessage {
	implicit val pickler: Pickler[ServerMessage] = PicklerGenerator.generatePickler[ServerMessage]
}

case class GameFound(players: Seq[Team]) extends ServerMessage
