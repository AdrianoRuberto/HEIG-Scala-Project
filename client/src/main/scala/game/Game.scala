package game

import engine.Engine
import game.entities.{Character, DebugStats, Player, PlayerFrame}
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

		val enemy = new Character(0) {
			setPosition(500, 500)
		}
		engine.registerEntity(enemy)

		val player = new Player
		engine.registerEntity(player)
		engine.camera.follow(player)

		engine.registerEntity(new PlayerFrame(85, -80, player))
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()
	def stop(): Unit = engine.stop()

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		case ServerMessage.SetGameMap(map) => println(map.shapes)
		case anything => println(anything)
	}
}
