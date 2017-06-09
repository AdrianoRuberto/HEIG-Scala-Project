package game.doodads.hud

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import org.scalajs.dom

class VictoryScreenEntity (msg: String, color: String) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Overlay
	val boundingBox: Rectangle = Rectangle(0, 0, 0, 0)

	private val start: Double = dom.window.performance.now()
	private def timing: Double = (dom.window.performance.now() - start) / 50.0

	def draw(ctx: CanvasCtx): Unit = {
		val width = ctx.canvas.width + 2
		val height = ctx.canvas.height + 2
		val now = timing

		ctx.globalAlpha = if (now < 50) (now min 10) / 10 * 0.8 else (0.8 + (now - 50) / 30 * 0.2) min 1.0
		ctx.fillStyle = "rgb(238, 238, 238)"
		ctx.fillRect(-1, -1, width, height)

		if (now > 10) {
			if (now > 70) ctx.globalAlpha = (1.0 - (now - 70) / 25) max 0.0
			ctx.translate(width / 2.0, height / 2.0)
			val factor = Math.sin(((now min 40) - 10) / 30 * Math.PI / 2)
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
