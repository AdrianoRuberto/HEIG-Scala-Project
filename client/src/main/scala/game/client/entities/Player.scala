package game.client.entities

import engine.geometry.{Shape, Vector2D}
import engine.utils.CollisionDetection
import game.client.{Game, Server}
import game.protocol.ClientMessage
import game.skeleton.concrete.CharacterSkeleton
import org.scalajs.dom

class Player (skeleton: CharacterSkeleton, walls: => Iterable[Shape]) extends Character(skeleton, 1) {
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

				val from = skeleton.position
				val tx = from.x + Math.cos(angle) * speed * 1.2
				val ty = from.y + Math.sin(angle) * speed * 1.2
				val dest = CollisionDetection.collide(from.x, from.y, tx, ty, walls)

				movingDirection = direction
				movingThrottle = now
				movingSpeed = speed
				move(from, dest, speed)
			}
		} else if (moving) {
			stop()
		}

		val state = engine.mouse.left
		if (state != mouseState) {
			if (state) Game.spellKeyDown(0)()
			else Game.spellKeyUp(0)()
			mouseState = state
		}

		super.update(dt)
	}

	private def move(from: Vector2D, to: Vector2D, speed: Double): Unit = {
		val duration = (from <-> to) / speed * 1000
		skeleton.x.commit().interpolate(to.x, duration)
		skeleton.y.commit().interpolate(to.y, duration)
		skeleton.moving.value = true
		Server ! ClientMessage.Moving(to.x, to.y, duration, skeleton.x.serial, skeleton.y.serial)
	}

	private def stop(): Unit = {
		moving = false
		movingDirection = (0, 0)
		skeleton.x.commit().stop()
		skeleton.y.commit().stop()
		skeleton.moving.value = false
		Server ! ClientMessage.Stopped(
			skeleton.x.current, skeleton.y.current,
			skeleton.x.serial, skeleton.y.serial
		)
	}
}
