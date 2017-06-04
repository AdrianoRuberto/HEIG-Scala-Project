package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry._
import engine.utils.Layer


class ShapeDrawer(val coloredShape: ColoredShape) extends Entity {
	val layer: Layer = Layer.World
	val boundingBox: Rectangle = coloredShape.boundingBox

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = coloredShape.color
		ctx.strokeStyle = coloredShape.color

		coloredShape.shape match {
			case Circle(x, y, radius) =>
				ctx.beginPath()
				ctx.arc(radius, radius, radius, 0, 2 * Math.PI)
				ctx.fill()
			case Rectangle(x, y, width, height) => ctx.fillRect(0, 0, width, height)
			case t @ Triangle(ax, ay, bx, by, cx, cy) =>
				ctx.beginPath()
				ctx.moveTo(ax - t.boundingBox.x, ay - t.boundingBox.y)
				ctx.lineTo(bx - t.boundingBox.x, by - t.boundingBox.y)
				ctx.lineTo(cx - t.boundingBox.x, cy - t.boundingBox.y)
				ctx.fill()
			case s @ Segment(x1, y1, x2, y2) =>
				ctx.beginPath()
				ctx.lineWidth = 1
				ctx.moveTo(x1 - s.boundingBox.x, y1 - s.boundingBox.y)
				ctx.lineTo(x2 - s.boundingBox.x, y2 - s.boundingBox.y)
				ctx.stroke()
		}
	}
}
