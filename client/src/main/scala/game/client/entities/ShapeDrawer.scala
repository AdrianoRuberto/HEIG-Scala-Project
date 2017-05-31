package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry._
import engine.utils.Layer


class ShapeDrawer(coloredShape: ColoredShape) extends Entity {
	def layer: Layer = Layer.World
	def boundingBox: Rectangle = coloredShape.boundingBox
	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = coloredShape.color

		coloredShape.shape match {
			case Circle(x, y, radius) =>
				ctx.beginPath()
				ctx.arc(x, y, radius, 0, 2 * Math.PI)
				ctx.fill()
			case Rectangle(x, y, width, height) => ctx.fillRect(x, y, width, height)
			case Triangle(ax, ay, bx, by, cx, cy) =>
				ctx.beginPath()
				ctx.moveTo(ax, ay)
				ctx.lineTo(bx, by)
				ctx.lineTo(cx, cy)
				ctx.fill()
			case _ =>
		}
	}
}
