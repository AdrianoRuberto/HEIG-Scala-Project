package game

import boopickle.DefaultBasic.{Pickler, PicklerGenerator}

/**
  * Created by Adriano on 17.05.2017.
  */
sealed trait ClientMessage

object ClientMessage {
	implicit val pickler: Pickler[ClientMessage] = PicklerGenerator.generatePickler[ClientMessage]
}

case class SearchGame(player: Player) extends ClientMessage
