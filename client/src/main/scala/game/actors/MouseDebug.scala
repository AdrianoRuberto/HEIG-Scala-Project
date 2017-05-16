package game.actors

import engine.CanvasCtx
import engine.actor.Actor
import engine.actor.feature.{Drawable, MousePosition}
import engine.utils.{Layer, Point}

class MouseDebug(x: Double, y: Double) extends Actor with MousePosition with Drawable {
	val position: Point = Point(x, y)
	val layer: Layer = Layer.Interface

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillText(mousePosition.toString.drop("Point".length), 0, 0)
	}
}
