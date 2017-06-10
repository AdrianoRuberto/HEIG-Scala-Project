package game.doodads.hud

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer

class OvertimeEntity (skeleton: OvertimeSkeleton) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(Center, 90, 150, 36)

	def draw(ctx: CanvasCtx): Unit = if (skeleton.enabled.value) {
		ctx.fillStyle = "#ffb56b"
		ctx.strokeStyle = "#ffb56b"
		ctx.lineWidth = 2
		ctx.fillRect(0, 0, 150 * (skeleton.left.current / 100), 15)
		ctx.strokeRect(0, 0, 150, 15)

		ctx.textAlign = "center"
		ctx.textBaseline = "hanging"
		ctx.font = "500 16px 'Roboto Mono'"
		ctx.fillText("OVERTIME", 75, 20)
	}
}
