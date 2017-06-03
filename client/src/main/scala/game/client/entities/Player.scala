package game.client.entities

import engine.Keyboard
import game.client.{Game, Server}
import game.protocol.ClientMessage
import game.skeleton.concrete.CharacterSkeleton
import org.scalajs.dom

class Player (skeleton: CharacterSkeleton) extends Character(skeleton, 1) {
	// Keyboard monitor for edge detection in key presses
	protected implicit val keyboardMonitor = new Keyboard.Monitor

	private var moving = false
	private var movingSpeed = 0.0
	private var movingDirection = (0, 0)
	private var movingThrottle = 0.0

	private var keyH = 0
	private var keyV = 0

	private var mouseState = false

	override protected def attached(): Unit = {
		engine.keyboard.registerKey("w", () => keyV -= 1, () => keyV += 1)
		engine.keyboard.registerKey("a", () => keyH -= 1, () => keyH += 1)
		engine.keyboard.registerKey("s", () => keyV += 1, () => keyV -= 1)
		engine.keyboard.registerKey("d", () => keyH += 1, () => keyH -= 1)
	}

	override protected def detached(): Unit = {
		engine.keyboard.unregisterKeys("w", "a", "s", "d")
	}

	override def update(dt: Double): Unit = {
		if (keyH != 0 || keyV != 0) {
			moving = true
			val direction = (keyH, keyV)
			val now = dom.window.performance.now()
			val speed = skeleton.speed.value
			if (direction != movingDirection || (now - movingThrottle) > 1000 || speed != movingSpeed) {
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

				val tx = skeleton.x.current + Math.cos(angle) * speed * 2
				val ty = skeleton.y.current + Math.sin(angle) * speed * 2

				skeleton.x.interpolate(tx, 2000)
				skeleton.y.interpolate(ty, 2000)

				movingDirection = direction
				movingThrottle = now
				movingSpeed = speed
				Server ! ClientMessage.Moving(tx, ty)
			}
		} else if (moving) {
			moving = false
			movingDirection = (0, 0)
			skeleton.x.stop()
			skeleton.y.stop()
			Server ! ClientMessage.Stopped(skeleton.x.current, skeleton.y.current)
		}

		val state = engine.mouse.left
		if (state != mouseState) {
			if (state) Game.spellKeyDown(0)()
			else Game.spellKeyUp(0)()
			mouseState = state
		}

		super.update(dt)
	}
}
