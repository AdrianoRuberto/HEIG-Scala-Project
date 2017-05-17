package game

import boopickle.Default._

sealed trait ClientMessage

object ClientMessage {
	case class SearchGame(player: Player) extends ClientMessage

	implicit val pickler: Pickler[ClientMessage] = generatePickler[ClientMessage]
}
