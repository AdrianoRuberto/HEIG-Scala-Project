package game.client.entities

import engine.Keyboard
import engine.geometry.Point
import game.client.Server
import game.protocol.ClientMessage
import game.skeleton.concrete.CharacterSkeleton
import org.scalajs.dom

class Player (skeleton: CharacterSkeleton) extends Character(skeleton, 1) {
	// Keyboard monitor for edge detection in key presses
	protected implicit val keyboardMonitor = new Keyboard.Monitor

	private var moving = false
	private var movingAngle = 0.0
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
			moving = true

			val Point(x, y) = engine.mouse.relative.point
			movingAngle = Math.atan2(y, x)
			val speed = skeleton.speed.value
			val tx = skeleton.x.current + Math.cos(movingAngle) * speed
			val ty = skeleton.y.current + Math.sin(movingAngle) * speed

			skeleton.x.interpolate(tx, 1000)
			skeleton.y.interpolate(ty, 1000)

			val now = dom.window.performance.now()
			if (now - movingThrottle > 40) {
				// Remote movement is 25 Hz
				movingThrottle = now
				Server ! ClientMessage.Moving(movingAngle)
			}
		} else if (moving) {
			moving = false
			skeleton.x.stop()
			skeleton.y.stop()
			Server ! ClientMessage.Stopped(skeleton.x.current, skeleton.y.current)
		}

		super.update(dt)
	}
}
