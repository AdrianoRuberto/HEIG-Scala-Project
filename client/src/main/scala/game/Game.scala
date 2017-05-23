package game

import engine.Engine
import engine.entity.feature.{MousePosition, Updatable}
import engine.geometry.{Point, Size}
import engine.utils.MouseButtons
import game.actors.{MouseDebug, Player}
import game.protocol.ServerMessage
import org.scalajs.dom
import org.scalajs.dom.html

object Game {
	private lazy val canvas = dom.document.querySelector("#canvas").asInstanceOf[html.Canvas]
	private lazy val engine = new Engine(canvas)

	def setup(): Unit = {
		dom.window.on(Event.Resize) { _ => resizeCanvas() }
		resizeCanvas()
		engine.setup()
	}

	def resizeCanvas(): Unit = {
		canvas.width = dom.window.innerWidth.toInt
		canvas.height = dom.window.innerHeight.toInt
	}

	def start(): Unit = {
		engine.start()

		engine.registerActor(new MouseDebug(5, 13))
		engine.registerActor(new Player(1) with MousePosition with Updatable {
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
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()
	def stop(): Unit = engine.stop()

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		case ServerMessage.SetGameMap(map) => println(map.shapes)
		case anything => println(anything)
	}
}
