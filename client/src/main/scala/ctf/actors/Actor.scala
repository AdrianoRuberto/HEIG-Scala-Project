package ctf
package actors

import ctf.utils.{Layer, Point}

abstract class Actor(val layer: Layer) {
	def position: Point
	def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = ()
	def update(dt: Double): Unit = ()
	def draw(ctx: CanvasCtx): Unit
}

object Actor {
	implicit val actorOrdering: Ordering[Actor] = Ordering.by(_.layer.strata)
}
