package game.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.{AbsolutePosition, Drawable}
import engine.geometry.Rectangle
import engine.utils.Layer

class PlayerFrame (x: Double, y: Double, player: Character) extends Entity
		with Drawable with AbsolutePosition {
	val boundingBox: Rectangle = Rectangle(x, y, 200, 80)
	val layer: Layer = Layer.Interface

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, -0.05, 0, 1, -0.5, 0)

		ctx.font = "500 24px 'Roboto Mono'"
		ctx.textAlign = "right"
		ctx.fillText(player.health.ceil.toString, 45, 30)

		ctx.font = "400 14px 'Roboto Mono'"
		ctx.textAlign = "left"
		ctx.fillText("/ " + player.healthMax.ceil, 52, 30)

		val splits = (player.healthMax / 25).round.toInt
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
		ctx.fillStyle = "rgba(17, 17, 17, 0.1)"
		ctx.fillRect(0, 45, 200, 25)
		ctx.fillStyle = player.healthColor
		ctx.fillRect(0, 45, 200 * (player.health / player.healthMax), 25)
		ctx.restore()

		drawSegments()
		ctx.strokeStyle = "#444"
		ctx.stroke()
	}
}
