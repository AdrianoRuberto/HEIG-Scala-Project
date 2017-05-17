package game

import boopickle.Default._

/**
  * Created by Adriano on 17.05.2017.
  */
sealed trait ClientMessage

case class SearchGame(player: Player) extends ClientMessage

object ClientMessage {
	implicit val pickler: Pickler[ClientMessage] = generatePickler[ClientMessage]
}