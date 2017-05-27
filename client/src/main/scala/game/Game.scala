package game

import engine.Engine
import game.entities.{Character, DebugStats, Player, PlayerFrame, TeamFrame}
import game.protocol.ServerMessage
import org.scalajs.dom
import org.scalajs.dom.html
import utils.PersistentBoolean

object Game {
	private lazy val canvas = dom.document.querySelector("#canvas").asInstanceOf[html.Canvas]
	private lazy val engine = new Engine(canvas)

	val debugStatsShown = PersistentBoolean("displayStats", default = false)
	private lazy val debugStatsEntity = new DebugStats(10, 10)

	def setup(): Unit = {
		dom.window.on(Event.Resize) { _ => resizeCanvas() }
		resizeCanvas()
		engine.setup()
		engine.keyboard.registerCommandKey("s")(toggleDebugStats())
		if (debugStatsShown) engine.registerEntity(debugStatsEntity)
	}

	def resizeCanvas(): Unit = {
		canvas.width = dom.window.innerWidth.toInt
		canvas.height = dom.window.innerHeight.toInt
	}

	def start(): Unit = {
		engine.start()
		engine.setWorldSize(2000, 2000)

		val enemy = new Character("Malevolent foe", 0) {
			setPosition(500, 500)
		}
		engine.registerEntity(enemy)

		val a = new Character("A Bot", 0) {
			healthColor = "#5a5"
			setPosition(100, 123)
		}

		val b = new Character("Heregellas", 0) {
			healthColor = "#5a5"
			setPosition(248, 46)
		}

		val c = new Character("0123456789", 0) {
			healthColor = "#5a5"
			setPosition(144, 178)
		}


		engine.registerEntity(a)
		engine.registerEntity(b)
		engine.registerEntity(c)

		val player = new Player("Blash")
		engine.registerEntity(player)
		engine.camera.follow(player)

		engine.registerEntity(new TeamFrame(10, if (debugStatsShown) 35 else 10, Seq(player, a, b, c)))

		engine.registerEntity(new PlayerFrame(85, -80, player))
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()

	def stop(): Unit = {
		engine.stop()
		engine.unregisterAllEntities()
	}

	def toggleDebugStats(): Unit = {
		if (debugStatsShown) engine.unregisterEntity(debugStatsEntity)
		else engine.registerEntity(debugStatsEntity)
		debugStatsShown.toggle()
	}

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		case ServerMessage.SetGameMap(map) => println(map.shapes)
		case anything => println(anything)
	}
}
