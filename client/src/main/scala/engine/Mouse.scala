package engine

import engine.entity.Entity
import engine.geometry.Point
import engine.utils.MouseButtons
import org.scalajs.dom

final class Mouse private[engine] (engine: Engine) {
	private var rawX: Double = 0
	private var rawY: Double = 0

	def x: Double = (rawX + engine.camera.left + 0.5).floor
	def y: Double = (rawY + engine.camera.top + 0.5).floor

	def point: Point = Point(x, y)

	var left: Boolean = false

	object relative {
		def x(implicit to: Entity): Double = {
			val box = to.boundingBox
			Mouse.this.x - (box.left + box.width / 2 + 0.5).floor
		}

		def y(implicit to: Entity): Double = {
			val box = to.boundingBox
			Mouse.this.y - (box.top + box.height / 2 + 0.5).floor
		}

		def point(implicit to: Entity): Point = Point(x, y)
	}

	private[engine] def handler(event: dom.MouseEvent): Unit = {
		val rect = engine.canvas.getClientRects()(0)
		rawX = event.clientX - rect.left
		rawY = event.clientY - rect.top
		left = (event.buttons & MouseButtons.Left) != 0
	}
}
