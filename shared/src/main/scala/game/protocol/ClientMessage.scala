package game.protocol

import boopickle.Default._
import game.shared.UID

sealed trait ClientMessage

object ClientMessage {
	case class SearchGame(name: String, fast: Boolean) extends ClientMessage

	private implicit val UIDPicker = UID.pickler
	implicit val pickler: Pickler[ClientMessage] = generatePickler[ClientMessage]
}
