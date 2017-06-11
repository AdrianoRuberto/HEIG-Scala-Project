package game.doodads.hud

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer

class DeathScreenEntity (duration: Double) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Overlay
	val boundingBox: Rectangle = Rectangle(0, 0, 0, 0)

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = "rgba(17, 17, 17, 0.9)"
		ctx.fillRect(-1, -1, ctx.canvas.width + 2, ctx.canvas.height + 2)
	}
}
