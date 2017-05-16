package engine
package actor
package feature

import engine.actor.generics.GenericMouseHandler
import engine.utils.Point

/**
  * An actor feature adding mouse position tracking to the actor
  */
trait MousePosition extends Actor with MouseEvents with GenericMouseHandler {
	private var mouseX: Double = 0
	private var mouseY: Double = 0

	override def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = {
		mouseX = x
		mouseY = y
		super.handleMouse(tpe, x, y, button)
	}

	/** The current mouse position, relative to the display canvas */
	def mousePosition: Point = {
		Point(mouseX, mouseY)
	}

	/** The current mouse position, relative to this actor */
	def relativeMousePosition(implicit ev: this.type <:< Positioned): Point = {
		val Point(a, b) = this.position
		val Point(x, y) = mousePosition
		Point(x - a, y - b)
	}
}
