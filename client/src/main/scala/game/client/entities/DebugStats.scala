package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.{AbsolutePosition, Updatable}
import engine.geometry.Rectangle
import engine.utils.Layer
import game.client.Server

class DebugStats(x: Double, y: Double) extends Entity with AbsolutePosition with Updatable {

	val boundingBox: Rectangle = Rectangle(x, y, 500, 15)
	val layer: Layer = Layer.Interface

	private var frames: Int = 0
	private var dts: Double = 0.0
	private var fps: Double = 0.0
	private var cpu: Double = 0.0
	private var draw: Double = 0.0

	private var statFPS: String = "FPS: 0".padTo(10, ' ')
	private var statCPU: String = "CPU: 0%".padTo(10, ' ')
	private var statDRW: String = "DRW: 0%".padTo(10, ' ')
	private var statLAT: String = "LAT: 0".padTo(10, ' ')

	private var text: String = ""

	def update(dt: Double): Unit = {
		frames += 1
		dts += dt
		if (dts > 500) {
			fps = (frames * 1000 / dts).round
			frames = 0
			dts = 0

			cpu = (fps * engine.cpuTime / 10).floor
			draw = (engine.drawTime / nz(engine.cpuTime) * 100).floor

			statFPS = s"FPS: $fps".padTo(10, ' ')
			statCPU = s"CPU: $cpu%".padTo(10, ' ')
			statDRW = s"DRW: $draw%".padTo(10, ' ')
			statLAT = s"LAT: ${Server.latency.floor}".padTo(10, ' ')
		}

		// Camera
		val camBox = engine.camera.box
		val camX = (camBox.x + camBox.width / 2).floor
		val camY = (camBox.y + camBox.height / 2).floor
		val statCAM = s"Cam: $camX, $camY".padTo(20, ' ')

		// Mouse
		val statMOUSE = s"Mouse: ${engine.mouse.x.floor}, ${engine.mouse.y.floor}".padTo(20, ' ')

		// Whole text
		text = s"$statFPS $statCPU $statDRW $statLAT $statCAM $statMOUSE"
	}

	@inline private def nz(value: Double): Double = value + Double.MinPositiveValue

	def draw(ctx: CanvasCtx): Unit = {
		// Background
		ctx.fillStyle = "rgba(17, 17, 17, 0.1)"
		ctx.fillRect(0, 0, 500, 15)

		// Text
		ctx.fillStyle = "black"
		ctx.font = "400 10px 'Roboto Mono'"
		ctx.textBaseline = "hanging"
		ctx.fillText(text, 3, 3)
	}
}
