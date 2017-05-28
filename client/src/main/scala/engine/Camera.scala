package engine

import engine.entity.Entity
import engine.geometry.{Point, Rectangle}

final class Camera private[engine] (engine: Engine) {
	private var following: Entity = null

	private var smoothing: Boolean = false
	private var tx: Double = 0.0
	private var ty: Double = 0.0

	private var velocity: Boolean = false
	private var speed: Double = 0.0

	private var x: Double = 0.0
	private var y: Double = 0.0

	private[engine] var left: Double = 0.0
	private[engine] var top: Double = 0.0

	@inline def box: Rectangle = Rectangle(left, top, engine.canvas.width, engine.canvas.height)

	private[engine] def update(dt: Double): Unit = {
		if (following != null) {
			val target = following.boundingBox
			tx = target.x + target.width / 2
			ty = target.y + target.height / 2
		}

		if (smoothing) {
			val dx = tx - x
			val dy = ty - y
			if (velocity) {
				val norm = Math.sqrt(dx * dx + dy + dy)
				val actual = norm max (dt * speed / 1000)
				x += (dx / norm * actual)
				y += (dy / norm * actual)
			} else {
				x += dx / 3
				y += dy / 3
			}
		} else {
			x = tx
			y = ty
		}

		left = x - engine.canvas.width / 2
		top = y - engine.canvas.height / 2
	}

	def setPoint(a: Double, b: Double): Unit = {
		following = null
		tx = a
		ty = b
	}

	@inline def setPoint(p: Point): Unit = setPoint(p.x, p.y)

	def follow(entity: Entity): Unit = following = entity
	def detach(): Unit = following = null

	def setSmoothing(state: Boolean): Unit = {
		smoothing = state
		velocity = false
	}

	def setSmoothingSpeed(pps: Double): Unit = {
		speed = pps
		smoothing = true
		velocity = true
	}
}
