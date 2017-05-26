package game.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.{AbsolutePosition, Drawable}
import engine.geometry.Rectangle
import engine.utils.Layer

class PlayerFrame (x: Double, y: Double, player: Player) extends Entity
		with Drawable with AbsolutePosition {
	val boundingBox: Rectangle = Rectangle(x, y, 200, 90)
	val layer: Layer = Layer.Interface

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, -0.05, 0, 1, -0.5, 0)

		ctx.font = "500 24px 'Roboto Mono'"
		ctx.textAlign = "right"
		ctx.fillText(player.health.smoothValue.ceil.toString, 45, 30)

		ctx.font = "400 14px 'Roboto Mono'"
		ctx.textAlign = "left"
		ctx.fillText("/ " + player.health.max.ceil, 52, 30)

		val splits = (player.health.max / 25).round.toInt
		val blanks = (splits - 1) * 5
		val width = (200.0 - blanks) / splits

		def drawSegments(): Unit = {
			ctx.beginPath()
			for (i <- 1 to splits) {
				ctx.rect((i - 1) * (width + 5), 45, width, 25)
			}
		}

		ctx.save()
		drawSegments()
		ctx.clip()
		ctx.fillStyle = "rgba(216, 216, 216, 0.9)"
		ctx.fillRect(0, 45, 200, 25)
		ctx.fillStyle = player.healthColor
		ctx.fillRect(0, 45, 200 * player.health.smoothPercent, 25)
		ctx.restore()

		drawSegments()
		ctx.strokeStyle = "#444"
		ctx.stroke()

		// Energy
		ctx.fillStyle = "rgba(216, 216, 216, 0.7)"
		ctx.fillRect(0, 80, 200, 10)

		ctx.fillStyle = "#fc2"
		ctx.fillRect(0, 80, 200 * player.energy.smoothPercent, 10)

		ctx.strokeRect(0, 80, 200, 10)
	}
}
