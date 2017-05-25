package engine
package modules

import org.scalajs.dom

trait EngineLoop { this: Engine =>
	private var running = false
	private var locked = true

	def isRunning: Boolean = running
	def isLocked: Boolean = locked

	private var timestamp: Double = Double.NaN
	def time: Double = timestamp

	def loop(now: Double): Unit = if (running) {
		// Compute delta time
		val dt = if (timestamp.isNaN) 0 else now - timestamp
		timestamp = now

		// Update and draw everything
		for (entity <- updatableEntities) entity.update(dt)
		camera.update(dt)
		drawVisibleEntities()

		// Request a new animation frame
		dom.window.requestAnimationFrame(loop _)
	}

	def start(): Unit = {
		running = true
		timestamp = Double.NaN
		dom.window.requestAnimationFrame(loop _)
	}

	def lock(): Unit = locked = true
	def unlock(): Unit = locked = false

	def stop(): Unit = {
		running = false
		ctx.clearRect(0, 0, canvas.width, canvas.height)
	}
}
