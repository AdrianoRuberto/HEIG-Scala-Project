package game.actors

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.Drawable
import engine.utils.{Layer, Size}

abstract class Player(sublayer: Int = 0) extends Entity with Drawable {
	val layer: Layer = Layer.Players / sublayer

	def size: Size
	def facing: Double

	def draw(ctx: CanvasCtx): Unit = {
		val Size(width, height) = size

		ctx.strokeStyle = "red"
		ctx.fillStyle = "red"
		ctx.lineWidth = 2

		ctx.rotate(facing - Math.PI / 2)
		ctx.translate(-width / 2, -height / 2)

		ctx.strokeRect(0, 0, width, height)

		ctx.fillStyle = "red"
		ctx.fillRect(width / 4, height - 10, 4, 4)

		ctx.fillStyle = "red"
		ctx.fillRect(width / 4 * 3 - 4, height - 10, 4, 4)
	}
}
