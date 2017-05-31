package game.client.entities

import engine.Keyboard
import game.client.Server
import game.protocol.ClientMessage
import game.skeleton.concrete.CharacterSkeleton
import org.scalajs.dom

class Player (skeleton: CharacterSkeleton) extends Character(skeleton, 1) {
	// Keyboard monitor for edge detection in key presses
	protected implicit val keyboardMonitor = new Keyboard.Monitor

	private var moving = false
	private var movingDirection = (0, 0)
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
		val w = engine.keyboard.key("w")
		val a = engine.keyboard.key("a")
		val s = engine.keyboard.key("s")
		val d = engine.keyboard.key("d")

		val h = if (a && !d) -1 else if (!a && d) 1 else 0
		val v = if (w && !s) -1 else if (!w && s) 1 else 0

		if (h != 0 || v != 0) {
			moving = true
			val direction = (h, v)
			val now = dom.window.performance.now()
			if (direction != movingDirection || (now - movingThrottle) > 500) {
				val angle = direction match {
					case (-1, 0) => Math.PI
					case (1, 0) => 0
					case (0, -1) => -Math.PI / 2
					case (0, 1) => Math.PI / 2
					case (-1, -1) => Math.PI / 4 * -3
					case (-1, 1) => Math.PI / 4 * 3
					case (1, -1) => -Math.PI / 4
					case (1, 1) => Math.PI / 4
				}

				val speed = skeleton.speed.value
				val tx = skeleton.x.current + Math.cos(angle) * speed
				val ty = skeleton.y.current + Math.sin(angle) * speed

				skeleton.x.interpolate(tx, 1000)
				skeleton.y.interpolate(ty, 1000)

				movingDirection = direction
				movingThrottle = now
				Server ! ClientMessage.Moving(tx, ty)
			}
		} else if (moving) {
			moving = false
			movingDirection = (0, 0)
			skeleton.x.stop()
			skeleton.y.stop()
			Server ! ClientMessage.Stopped(skeleton.x.current, skeleton.y.current)
		}

		super.update(dt)
	}
}
