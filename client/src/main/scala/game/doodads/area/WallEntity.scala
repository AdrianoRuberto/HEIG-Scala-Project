package game.doodads.area

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.{Rectangle, Shape}
import engine.utils.Layer
import utils.Color

class WallEntity (shape: Shape, color: Color) extends Entity {
	val layer: Layer = Layer.World
	val boundingBox: Rectangle = shape.boundingBox

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = color.toString
		ctx.strokeStyle = color.toString
		ctx.lineWidth = 2
		ctx.beginPath()
		drawShape(ctx, shape)
		ctx.fill()
		ctx.stroke()
	}
}
