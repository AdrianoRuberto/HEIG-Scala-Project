package game.doodads.area

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.{Rectangle, Shape}
import engine.utils.Layer
import utils.Color

class StaticAreaEntity (shape: Shape, fill: Boolean, stroke: Boolean,
                        fillColor: Color, strokeColor: Color, strokeWidth: Int) extends Entity {

	val layer: Layer = Layer.LowFx
	val boundingBox: Rectangle = shape.boundingBox

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = fillColor.toString
		ctx.strokeStyle = strokeColor.toString
		ctx.lineWidth = strokeWidth

		ctx.beginPath()
		drawShape(ctx, shape)
		if (fill) ctx.fill()
		if (stroke) ctx.stroke()
	}
}
