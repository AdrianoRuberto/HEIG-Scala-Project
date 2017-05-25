package engine

import engine.entity.Entity
import engine.entity.feature.Position
import org.scalajs.dom

final class Mouse private[engine] (engine: Engine) {
	private var rawX: Double = 0
	private var rawY: Double = 0

	def x: Double = rawX + engine.camera.left
	def y: Double = rawY + engine.camera.top

	object relative {
		def x(implicit to: Entity with Position): Double = {
			val box = to.boundingBox
			Mouse.this.x - (box.left + box.width / 2)
		}

		def y(implicit to: Entity with Position): Double = {
			val box = to.boundingBox
			Mouse.this.y - (box.top + box.height / 2)
		}
	}

	def handler(event: dom.MouseEvent): Unit = {
		val rect = engine.canvas.getClientRects()(0)
		rawX = event.clientX - rect.left
		rawY = event.clientY - rect.top
	}
}
