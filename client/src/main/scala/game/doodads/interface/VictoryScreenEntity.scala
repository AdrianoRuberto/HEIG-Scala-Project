package game.doodads.interface

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.skeleton.concrete.ProgressSkeleton

class VictoryScreenEntity (msg: String, color: String, progress: ProgressSkeleton) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Overlay
	val boundingBox: Rectangle = Rectangle(0, 0, 0, 0)

	def draw(ctx: CanvasCtx): Unit = {
		val width = ctx.canvas.width + 2
		val height = ctx.canvas.height + 2
		val timing = progress.value

		ctx.globalAlpha = if (timing < 50) (timing min 10) / 10 * 0.8 else (0.8 + (timing - 50) / 30 * 0.2) min 1.0
		ctx.fillStyle = "rgb(238, 238, 238)"
		ctx.fillRect(-1, -1, width, height)

		if (timing > 10) {
			if (timing > 70) ctx.globalAlpha = (1.0 - (timing - 70) / 25) max 0.0
			ctx.translate(width / 2.0, height / 2.0)
			val factor = Math.sin(((timing min 40) - 10) / 30 * Math.PI / 2)
			ctx.scale(factor, factor)
			ctx.textAlign = "center"
			ctx.textBaseline = "middle"
			ctx.font = "500 72px 'Roboto Mono'"
			ctx.fillStyle = color
			ctx.strokeStyle = "#333"
			ctx.lineWidth = 2
			ctx.fillText(msg, 0, 0, width - 50)
			ctx.strokeText(msg, 0, 0, width - 50)
		}
	}
}
