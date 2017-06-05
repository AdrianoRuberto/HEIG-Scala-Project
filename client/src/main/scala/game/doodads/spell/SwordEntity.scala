package game.doodads.spell

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.Rectangle
import engine.utils.Layer
import game.spells.icons.{Sword => SwordIcon}
import java.lang.Math._
import scala.util.Random

class SwordEntity (x: Double, y: Double, baseAngle: Double) extends Entity {
	val layer: Layer = Layer.HighFx
	val boundingBox: Rectangle = Rectangle(x - 100, y - 100, 200, 200)

	private val start = System.currentTimeMillis()
	private val direction = if (Random.nextBoolean()) PI / 2 else -PI / 2
	private val initial = baseAngle - direction / 2
	private val duration = 300.0

	def draw(ctx: CanvasCtx): Unit = {
		val dt = System.currentTimeMillis() - start
		if (dt < duration) {
			val progress = dt / duration
			val angle = initial + progress * direction
			ctx.globalAlpha = sin(progress * PI)
			ctx.translate(100, 100)
			ctx.rotate(angle)
			ctx.translate(50, 0)
			ctx.rotate(Math.PI / 4)
			ctx.translate(-30, -30)
			SwordIcon.drawIcon(ctx)
			ctx.fillStyle = "#333"
			ctx.strokeStyle = "#eee"
			ctx.fill()
			ctx.stroke()
		}
	}
}
