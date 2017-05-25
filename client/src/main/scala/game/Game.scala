package game

import engine.Engine
import engine.entity.feature.Updatable
import engine.geometry.{Point, Rectangle, Size}
import engine.utils.MouseButtons
import game.actors.{DebugStats, Player}
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
		engine.setWorldSize(2000, 2000)

		engine.registerEntity(new DebugStats(5, 5))

		val enemy = new Player(0) {
			val size: Size = Size(30, 30)
			val facing: Double = 0
			val boundingBox: Rectangle = Rectangle(Point(200, 200), size)
		}
		engine.registerEntity(enemy)

		val player = new Player(1) with Updatable {
			val size: Size = Size(30, 30)

			private def moveSpeed = 100 // pixels / seconds
			private var current = Point(canvas.width / 2, canvas.height / 2)
			private var destination = Point(canvas.width / 2, canvas.height / 2)
			private var lastFacing = 0.0

			def boundingBox: Rectangle = Rectangle(Point(current.x - size.width / 2, current.y - size.height / 2), size)

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

			def relativeMousePosition: Point = Point(engine.mouse.relative.x, engine.mouse.relative.y)
			def mousePosition: Point = Point(engine.mouse.x, engine.mouse.y)

			def facing: Double = relativeMousePosition match {
				case Point(a, b) if mousePosition != current =>
					val angle = Math.atan2(b, a)
					val delta = Math.atan2(Math.sin(angle - lastFacing), Math.cos(angle - lastFacing))
					lastFacing += (delta / 3)
					lastFacing
				case _ => lastFacing
			}

			def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = {
				//super.handleMouse(tpe, x, y, button)
				if ((button & MouseButtons.Left) != 0) {
					destination = Point(x, y)
				}
			}
		}

		engine.registerEntity(player)
		engine.camera.follow(player)
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()
	def stop(): Unit = engine.stop()

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		case ServerMessage.SetGameMap(map) => println(map.shapes)
		case anything => println(anything)
	}
}
