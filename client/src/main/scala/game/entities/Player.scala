package game.entities

import engine.Keyboard
import engine.geometry.Point

/**
  * Created by galedric on 26.05.2017.
  */
class Player extends Character(1) {
	// Keyboard monitor for edge detection in key presses
	protected implicit val keyboardMonitor = new Keyboard.Monitor

	// Energy
	val energy: Resource = createResource(100, 100, 15, smoothing = true)
	var sprinting = false

	color = "black"
	healthColor = "#5a5"
	health.value = 133
	health.max = 200

	override def update(dt: Double): Unit = {
		val Point(rx, ry) = engine.mouse.relative.point
		if (rx != 0 && ry != 0) tf = Math.atan2(ry, rx)

		val moving = tx != x || ty != y
		if (sprinting && (!engine.keyboard.shift || energy.value < 1 || !moving)) {
			sprinting = false
			speed /= 2
			energy.stopDrain(50)
		}
		else if (!sprinting && engine.keyboard.down("Shift") && energy.value > 1 && moving) {
			sprinting = true
			speed *= 2
			energy.startDrain(50)
		}

		if (engine.mouse.left) {
			tx = engine.mouse.x
			ty = engine.mouse.y
		}

		super.update(dt)
	}
}
