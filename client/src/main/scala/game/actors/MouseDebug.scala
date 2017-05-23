package game.actors

import engine.entity.Entity
import engine.entity.feature.{Drawable, MousePosition}
import engine.geometry.Point
import engine.utils.Layer

class MouseDebug(x: Double, y: Double) extends Entity with MousePosition with Drawable {
	val position: Point = Point(x, y)
	val layer: Layer = Layer.Interface

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillText(mousePosition.toString.drop("Point".length), 0, 0)
	}
}
