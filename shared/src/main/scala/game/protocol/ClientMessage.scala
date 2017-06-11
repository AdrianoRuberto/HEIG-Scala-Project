package game.protocol

import engine.geometry.Vector2D
import macros.pickle

@pickle sealed trait ClientMessage

object ClientMessage {
	@pickle case class Ping(payload: Long) extends ClientMessage with SystemMessage
	@pickle case class SearchGame(name: String, fast: Boolean) extends ClientMessage

	sealed trait GameMessage extends ClientMessage
	@pickle case class Moving(x: Double, y: Double, duration: Double, xs: Int, ys: Int) extends GameMessage
	@pickle case class Stopped(x: Double, y: Double, xs: Int, ys: Int) extends GameMessage
	@pickle case class SpellCast(slot: Int, point: Vector2D) extends GameMessage
	@pickle case class SpellCancel(slot: Int, point: Vector2D) extends GameMessage
}
