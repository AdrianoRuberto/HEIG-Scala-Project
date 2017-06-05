package game.doodads.status

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.UID
import game.skeleton.concrete.KothStatusSkeleton

class KothStatusEntity (skeleton: KothStatusSkeleton) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(Center, 20, 265, 50)

	private val colorA = "rgba(119, 119, 255, 0.8)"
	private val colorB = "rgba(255, 85, 85, 0.8)"
	private val colorDefault = "rgba(238, 238, 238, 0.9)"

	private val teamA: UID = skeleton.teamA.value
	private val teamB: UID = skeleton.teamB.value

	private def capture: Double = skeleton.capture.value
	private def controlling: UID = skeleton.controlling.value
	private def uncontrolled: Boolean = controlling == UID.zero
	private def controllingColor: String = if (controlling == teamA) colorA else colorB

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, 0, -0.1, 1, 5, 0)

		ctx.lineWidth = 2
		ctx.font = "500 25px 'Roboto Mono'"
		ctx.textAlign = "center"
		ctx.textBaseline = "middle"

		// Team A
		ctx.beginPath()
		ctx.fillStyle = if (controlling == teamA) colorA else colorDefault
		ctx.strokeStyle = colorA
		ctx.rect(0, 0, 100, 50)
		ctx.fill()
		ctx.stroke()
		ctx.fillStyle = if (controlling == teamA) "#fff" else colorA
		ctx.fillText(skeleton.progressA.value.floor + "%", 50, 25)

		// Team B
		ctx.beginPath()
		ctx.fillStyle = if (controlling == teamB) colorB else colorDefault
		ctx.strokeStyle = colorB
		ctx.rect(160, 0, 100, 50)
		ctx.fill()
		ctx.stroke()
		ctx.fillStyle = if (controlling == teamB) "#fff" else colorB
		ctx.fillText(skeleton.progressB.value.floor + "%", 210, 25)

		// Center
		ctx.beginPath()
		ctx.rect(105, 0, 50, 50)
		ctx.fillStyle = colorDefault
		ctx.fill()
		if (capture > 0) {
			ctx.fillStyle = colorA
			ctx.fillRect(105, 0, 50 * capture / 100, 50)
		} else {
			ctx.fillStyle = colorB
			ctx.fillRect(155, 0, 50 * capture / 100, 50)
		}
		ctx.strokeStyle = if (uncontrolled) "rgba(100, 100, 100, 0.9)" else controllingColor
		ctx.stroke()
	}
}
