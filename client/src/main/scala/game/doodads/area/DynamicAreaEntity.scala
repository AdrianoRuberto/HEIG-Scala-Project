package game.doodads.area

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.{Circle, Rectangle, Segment, Triangle}
import engine.utils.Layer
import game.skeleton.concrete.DynamicAreaSkeleton

class DynamicAreaEntity (skeleton: DynamicAreaSkeleton) extends Entity {
	val layer: Layer = Layer.LowFx
	def boundingBox: Rectangle = skeleton.shape.value.boundingBox

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = skeleton.fillColor.value
		ctx.strokeStyle = skeleton.strokeColor.value
		ctx.lineWidth = skeleton.strokeWidth.value

		ctx.beginPath()
		skeleton.shape.value match {
			case Circle(x, y, radius) =>
				ctx.arc(radius, radius, radius, 0, 2 * Math.PI)

			case Rectangle(x, y, width, height) =>
				ctx.rect(0, 0, width, height)

			case t @ Triangle(ax, ay, bx, by, cx, cy) =>
				ctx.moveTo(ax - t.boundingBox.x, ay - t.boundingBox.y)
				ctx.lineTo(bx - t.boundingBox.x, by - t.boundingBox.y)
				ctx.lineTo(cx - t.boundingBox.x, cy - t.boundingBox.y)

			case s @ Segment(x1, y1, x2, y2) =>
				ctx.moveTo(x1 - s.boundingBox.x, y1 - s.boundingBox.y)
				ctx.lineTo(x2 - s.boundingBox.x, y2 - s.boundingBox.y)
		}
		if (skeleton.fill.value) ctx.fill()
		if (skeleton.stroke.value) ctx.stroke()
	}
}
