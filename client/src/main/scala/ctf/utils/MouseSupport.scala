package ctf.utils

import ctf.actors.Actor

trait MouseSupport extends Actor {
	private[this] var mouseX: Double = 0
	private[this] var mouseY: Double = 0

	abstract override def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = {
		mouseX = x
		mouseY = y
		super.handleMouse(tpe, x, y, button)
	}

	def mousePosition: Point = Point(mouseX, mouseY)

	def relativeMousePosition: Point = {
		val Point(a, b) = position
		val Point(x, y) = mousePosition
		Point(x - a, y - b)
	}
}
