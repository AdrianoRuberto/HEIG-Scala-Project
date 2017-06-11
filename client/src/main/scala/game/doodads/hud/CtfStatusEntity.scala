package game.doodads.hud

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.UID
import game.spells.icons.DropTheFlag
import scala.scalajs.js
import utils.Color

class CtfStatusEntity (skeleton: CtfStatusSkeleton) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(Center, 20, 465, 50)

	private def teamA: UID = skeleton.teamA.value
	private def teamB: UID = skeleton.teamB.value

	private def colorA: String = (skeleton.colorA.value * 0.8).toString
	private def colorB: String = (skeleton.colorB.value * 0.8).toString
	private val lightGray: String = Color(238, 238, 238, 0.9).toString
	private val darkGray: String = Color(100, 100, 100, 0.9).toString

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, 0, -0.1, 1, 5, 0)

		ctx.lineWidth = 2
		ctx.font = "500 25px 'Roboto Mono'"
		ctx.textAlign = "center"
		ctx.textBaseline = "middle"

		// A Score
		ctx.strokeStyle = colorA
		for (i <- 3 to 1 by -1) {
			ctx.beginPath()
			ctx.arc(15, 25, 15, 0, Math.PI * 2)
			ctx.fillStyle = lightGray
			ctx.fill()
			ctx.fillStyle = colorA
			ctx.stroke()
			if (skeleton.scoreA.value >= i) ctx.fill()
			ctx.translate(40, 0)
		}

		def drawFlagStatus(teamColor: String, controlling: Boolean): Unit = {
			ctx.beginPath()
			ctx.rect(0, 0, 50, 50)
			ctx.fillStyle = lightGray
			ctx.fill()
			ctx.fillStyle = teamColor
			ctx.stroke()
			ctx.translate(-5, -5)
			if (controlling) {
				DropTheFlag.drawFlag(ctx)
				ctx.asInstanceOf[js.Dynamic].fill("evenodd")
			} else {
				ctx.beginPath()
				DropTheFlag.drawFlag(ctx)
				ctx.fill()
			}
			ctx.translate(5, 5)
			ctx.translate(60, 0)
		}

		// A Flag
		drawFlagStatus(colorA, skeleton.controllingA.value)

		// Timer
		ctx.strokeStyle = darkGray
		ctx.fillStyle = lightGray
		ctx.beginPath()
		ctx.rect(0, 0, 100, 50)
		ctx.fill()
		ctx.stroke()
		ctx.fillStyle = darkGray

		val timeLeft = skeleton.timer.current.ceil.toInt
		val minutes = timeLeft / 60
		val seconds = timeLeft % 60
		ctx.fillText(f"$minutes:$seconds%02d", 50, 25, 90)
		ctx.translate(110, 0)

		// B Flag
		ctx.strokeStyle = colorB
		ctx.fillStyle = colorB
		drawFlagStatus(colorB, skeleton.controllingB.value)

		// B Score
		for (i <- 1 to 3) {
			ctx.beginPath()
			ctx.arc(15, 25, 15, 0, Math.PI * 2)
			ctx.fillStyle = lightGray
			ctx.fill()
			ctx.fillStyle = colorB
			ctx.stroke()
			if (skeleton.scoreB.value >= i) ctx.fill()
			ctx.translate(40, 0)
		}
	}
}
