package game.protocol

import boopickle.Default._
import engine.geometry.Vector
import game.UID

sealed trait ClientMessage

object ClientMessage {
	case class Ping(payload: Long) extends ClientMessage with SystemMessage
	case class SearchGame(name: String, fast: Boolean) extends ClientMessage

	sealed trait GameMessage extends ClientMessage
	case class Moving(x: Double, y: Double) extends GameMessage
	case class Stopped(x: Double, y: Double) extends GameMessage
	case class SpellCast(slot: Int, point: Vector) extends GameMessage
	case class SpellCancel(slot: Int) extends GameMessage

	private implicit val UIDPicker = UID.pickler
	implicit val pickler: Pickler[ClientMessage] = generatePickler[ClientMessage]
}
