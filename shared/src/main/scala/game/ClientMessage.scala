package game

import boopickle.Default._

sealed trait ClientMessage

object ClientMessage {
	case class SearchGame(name: String) extends ClientMessage

	private implicit val UIDPicker = UID.pickler
	implicit val pickler: Pickler[ClientMessage] = generatePickler[ClientMessage]
}
