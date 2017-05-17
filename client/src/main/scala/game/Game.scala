package game

import engine.actor.feature.{MousePosition, Updatable}
import engine.utils.{MouseButtons, Point, Size}
import engine.{Canvas, Engine}
import game.actors.{MouseDebug, Player => APlayer}

object Game {
	def main(): Unit = {
		val canvas = Canvas("ctf-canvas", 1200, 800)
		val engine = new Engine(canvas)
		engine.registerActor(new MouseDebug(5, 13))

		engine.registerActor(new APlayer(1) with MousePosition with Updatable {
			val size: Size = Size(30, 30)

			private def moveSpeed = 100 // pixels / seconds
			private var current = Point(canvas.width / 2, canvas.height / 2)
			private var destination = Point(canvas.width / 2, canvas.height / 2)

			def position: Point = current

			override def update(dt: Double): Unit = if (current != destination) {
				val Point(cx, cy) = current
				val Point(dx, dy) = destination
				val mx = dx - cx
				val my = dy - cy
				val s = Math.sqrt(mx * mx + my * my)
				val n = moveSpeed * (dt / 1000)
				val z = s min n
				current = Point(cx + (mx / s * z), cy + (my / s * z))
			}

			def facing: Double = relativeMousePosition match {
				case Point(a, b) => Math.atan2(b, a)
			}

			override def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = {
				super.handleMouse(tpe, x, y, button)
				if ((button & MouseButtons.Left) != 0) {
					destination = Point(x, y)
				}
			}
		})

		engine.start()
	}
}
