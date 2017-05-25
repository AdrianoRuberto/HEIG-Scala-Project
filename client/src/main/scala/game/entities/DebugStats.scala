package game.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.{AbsolutePosition, Drawable, Updatable}
import engine.geometry.Rectangle
import engine.utils.Layer

class DebugStats(x: Double, y: Double, width: Int = 500, height: Int = 14) extends Entity
		with Drawable with AbsolutePosition with Updatable {

	val boundingBox: Rectangle = Rectangle(x, y, width, height)
	val layer: Layer = Layer.Interface

	private var fps: Double = 0
	private var frames: Int = 0
	private var dts: Double = 0

	def update(dt: Double): Unit = {
		frames += 1
		dts += dt
		if (dts > 500) {
			fps = (frames * 1000 / dts).round
			frames = 0
			dts = 0
		}
	}

	def draw(ctx: CanvasCtx): Unit = {
		// Background
		ctx.fillStyle = "rgba(17, 17, 17, 0.1)"
		ctx.fillRect(0, 0, 500, 14)

		// Text
		ctx.fillStyle = "black"
		ctx.font = "400 10px 'Roboto Mono'"
		ctx.textBaseline = "hanging"

		val cameraBox = engine.camera.box
		val camera = s"Cam: ${(cameraBox.x + cameraBox.width / 2).round}, " +
		             s"${(cameraBox.y + cameraBox.height / 2).round}".padTo(20, ' ')

		val mouse = s"Mouse: ${engine.mouse.x.round}, ${engine.mouse.y.round}".padTo(20, ' ')
		ctx.fillText(s"$camera $mouse FPS: $fps", 3, 3)
	}
}
