package game.doodads.interface

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer

class DeathScreenEntity(duration: Double) extends Entity with AbsolutePosition {
	def layer: Layer = Layer.Overlay
	def boundingBox: Rectangle = Rectangle(0, 0, 0, 0)
	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = "rgba(17, 17, 17, 0.9)"
		ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height)
	}
}
