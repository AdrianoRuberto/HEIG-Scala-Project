package game.doodads.area

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.{Rectangle, Shape}
import engine.utils.Layer

class StaticAreaEntity (shape: Shape, fill: Boolean, fillColor: String,
                        stroke: Boolean, strokeColor: String, strokeWidth: Int) extends Entity {

	val layer: Layer = Layer.LowFx
	val boundingBox: Rectangle = shape.boundingBox

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = fillColor
		ctx.strokeStyle = strokeColor
		ctx.lineWidth = strokeWidth

		ctx.beginPath()
		drawShape(ctx, shape)
		if (fill) ctx.fill()
		if (stroke) ctx.stroke()
	}
}
