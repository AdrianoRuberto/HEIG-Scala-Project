package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.skeleton.core.CharacterSkeleton

class PlayerFrame (x: Double, y: Double, skeleton: CharacterSkeleton) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(x, y, 200, 90)

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, -0.05, 0, 1, -0.5, 0)
		//ctx.strokeRect(0, 0, 200, 90)

		ctx.font = "500 24px 'Roboto Mono'"
		ctx.textAlign = "right"
		ctx.fillText(skeleton.health.current.ceil.toString, 45, 30)

		ctx.font = "400 14px 'Roboto Mono'"
		ctx.textAlign = "left"
		ctx.fillText("/ " + skeleton.health.max.ceil, 52, 30)

		val splits = (skeleton.health.max / 25 + 0.5).toInt
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
		ctx.fillStyle = "#5a5"
		ctx.fillRect(0, 45, 200 * skeleton.health.percent, 25)
		ctx.restore()

		drawSegments()
		ctx.strokeStyle = "#444"
		ctx.stroke()

		// Energy
		ctx.fillStyle = "rgba(216, 216, 216, 0.7)"
		ctx.fillRect(0, 80, 200, 10)

		ctx.fillStyle = "#fc2"
		ctx.fillRect(0, 80, 200 * skeleton.energy.percent, 10)

		ctx.strokeRect(0, 80, 200, 10)
	}
}
