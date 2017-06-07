package game.doodads.area

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.Rectangle
import engine.utils.Layer

class DynamicAreaEntity (skeleton: DynamicAreaSkeleton) extends Entity {
	val layer: Layer = Layer.LowFx
	def boundingBox: Rectangle = skeleton.shape.value.boundingBox

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = skeleton.fillColor.value.toString
		ctx.strokeStyle = skeleton.strokeColor.value.toString
		ctx.lineWidth = skeleton.strokeWidth.value

		ctx.beginPath()
		drawShape(ctx, skeleton.shape.value)
		if (skeleton.fill.value) ctx.fill()
		if (skeleton.stroke.value) ctx.stroke()
	}
}
