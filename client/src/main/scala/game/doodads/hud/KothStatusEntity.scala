package game.doodads.hud

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.UID
import utils.Color

class KothStatusEntity (skeleton: KothStatusSkeleton) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(Center, 20, 265, 50)

	private val colorA = Color(119, 119, 255, 0.8)
	private val colorB = Color(255, 85, 85, 0.8)
	private val colorDefault = Color(238, 238, 238, 0.9)

	private def teamA: UID = skeleton.teamA.value
	private def teamB: UID = skeleton.teamB.value

	private def capture: Double = skeleton.capture.current
	private def controlling: UID = skeleton.controlling.value
	private def uncontrolled: Boolean = controlling == UID.zero
	private def controllingColor: Color = if (controlling == teamA) colorA else colorB

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, 0, -0.1, 1, 5, 0)

		ctx.lineWidth = 2
		ctx.font = "500 25px 'Roboto Mono'"
		ctx.textAlign = "center"
		ctx.textBaseline = "middle"

		// Team A
		ctx.beginPath()
		ctx.fillStyle = if (controlling == teamA) colorA.toString else colorDefault.toString
		ctx.strokeStyle = colorA.toString
		ctx.rect(0, 0, 100, 50)
		ctx.fill()
		ctx.stroke()
		ctx.fillStyle = if (controlling == teamA) "#fff" else colorA.toString
		ctx.fillText(skeleton.progressA.current.floor + "%", 50, 25)

		// Team B
		ctx.beginPath()
		ctx.fillStyle = if (controlling == teamB) colorB.toString else colorDefault.toString
		ctx.strokeStyle = colorB.toString
		ctx.rect(160, 0, 100, 50)
		ctx.fill()
		ctx.stroke()
		ctx.fillStyle = if (controlling == teamB) "#fff" else colorB.toString
		ctx.fillText(skeleton.progressB.current.floor + "%", 210, 25)

		// Center
		ctx.beginPath()
		ctx.rect(105, 0, 50, 50)
		ctx.fillStyle = colorDefault.toString
		ctx.fill()
		if (capture > 0) {
			ctx.fillStyle = colorA.toString
			ctx.fillRect(105, 0, 50 * capture / 100, 50)
		} else {
			ctx.fillStyle = colorB.toString
			ctx.fillRect(155, 0, 50 * capture / 100, 50)
		}
		ctx.strokeStyle = if (uncontrolled) "rgba(100, 100, 100, 0.9)" else controllingColor.toString
		ctx.stroke()
	}
}
