package game.client.entities

import engine.Keyboard
import engine.geometry.Point
import game.skeleton.concrete.CharacterSkeleton
import org.scalajs.dom

class Player (skeleton: CharacterSkeleton) extends Character(skeleton, 1) {
	// Keyboard monitor for edge detection in key presses
	protected implicit val keyboardMonitor = new Keyboard.Monitor

	private var moving = false
	private var movingDirection = 0.0
	private var movingThrottle = 0.0
	private var sprinting = false

	override def update(dt: Double): Unit = {
		/*val Point(rx, ry) = engine.mouse.relative.point
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
*/
		if (engine.mouse.left) {
			val now = dom.window.performance.now()
			if (!moving || now - movingThrottle > 100) {
				movingThrottle = now
				moving = true
				val Point(x, y) = engine.mouse.relative.point
				val angle = Math.atan2(y, x)
				val speed = skeleton.speed.value
				val tx = skeleton.x.current + Math.cos(angle) * speed
				val ty = skeleton.y.current + Math.sin(angle) * speed
				skeleton.x.interpolate(tx, 1000)
				skeleton.y.interpolate(ty, 1000)
			}
		} else if (moving) {
			moving = false
			skeleton.x.stop()
			skeleton.y.stop()
		}

		super.update(dt)
	}
}
