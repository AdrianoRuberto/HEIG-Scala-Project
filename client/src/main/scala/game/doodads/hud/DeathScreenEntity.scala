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

		ctx.translate(ctx.canvas.width / 2, ctx.canvas.height / 2)
		ctx.textAlign = "center"
		ctx.textBaseline = "middle"
		ctx.font = "500 72px 'Roboto Mono'"
		ctx.fillStyle = "#eee"
		ctx.strokeStyle = "#000"
		ctx.lineWidth = 2
		ctx.fillText("YOU DIED!", 0, 0)
		ctx.strokeText("YOU DIED!", 0, 0)
	}
}
