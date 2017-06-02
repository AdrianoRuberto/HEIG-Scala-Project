package game.client

import engine.Engine
import engine.entity.Entity
import game.client.entities.{Character, DebugStats, Player, PlayerFrame, PlayerSpells, ShapeDrawer}
import game.protocol.ServerMessage._
import game.protocol.{ClientMessage, ServerMessage}
import game.skeleton.SkeletonManager
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.{TeamInfo, UID}
import org.scalajs.dom
import org.scalajs.dom.html
import utils.PersistentBoolean

object Game {
	private lazy val canvas = dom.document.querySelector("#canvas").asInstanceOf[html.Canvas]
	private lazy val engine = new Engine(canvas)

	private val debugStatsShown = PersistentBoolean("displayStats", default = false)
	private lazy val debugStatsEntity = new DebugStats(10, 10)

	private val skeletonManager = new SkeletonManager
	private var characterEntities: Map[UID, Entity] = Map.empty
	private var shapeEntities: Map[UID, Entity] = Map.empty

	private var teams: Seq[TeamInfo] = Nil
	private var playerUID: UID = UID.zero

	private val playerSpells: Array[Option[SpellSkeleton]] = Array.fill(4)(None)

	def setup(): Unit = {
		dom.window.on(Event.Resize) { _ => resizeCanvas() }
		resizeCanvas()
		engine.setup()
		engine.keyboard.registerKey("alt-s")(toggleDebugStats())
		engine.keyboard.registerKey("e", spellKeyDown(1), spellKeyUp(1))
		engine.keyboard.registerKey("q", spellKeyDown(2), spellKeyUp(2))
		engine.keyboard.registerKey("shift", spellKeyDown(3), spellKeyUp(3))
	}

	private def spellKeyDown(slot: Int)(): Unit = playerSpells(slot) match {
		case Some(skeleton) =>
			if (skeleton.cooldown.ready) {
				skeleton.activated.value = true
			}
			Server ! ClientMessage.SpellCast(slot)
		case _ => // Ignore
	}

	private def spellKeyUp(slot: Int)(): Unit = playerSpells(slot) match {
		case Some(skeleton) =>
			skeleton.activated.value = false
			Server ! ClientMessage.SpellCancel(slot)
		case _ => // Ignore
	}

	def resizeCanvas(): Unit = {
		canvas.width = dom.window.innerWidth.toInt
		canvas.height = dom.window.innerHeight.toInt
	}

	def start(teams: Seq[TeamInfo], me: UID): Unit = {
		this.teams = teams
		this.playerUID = me
		engine.start()
		if (debugStatsShown) {
			engine.registerEntity(debugStatsEntity)
		}
		/*
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

		engine.registerEntity(new PlayerFrame(85, -80, player))*/
	}

	def reset(): Unit = {
		engine.unregisterAllEntities()
		engine.camera.setSmoothing(false)
		skeletonManager.clear()
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()
	def stop(): Unit = engine.stop()

	private def toggleDebugStats(): Unit = {
		if (debugStatsShown) engine.unregisterEntity(debugStatsEntity)
		else engine.registerEntity(debugStatsEntity)
		debugStatsShown.toggle()
	}

	private def instantiateCharacter(characterUID: UID, skeletonUID: UID): Unit = {
		val skeleton = skeletonManager.getAs[CharacterSkeleton](skeletonUID)
		val entity = if (characterUID != playerUID) new Character(skeleton) else {
			val player = new Player(skeleton)
			engine.registerEntity(new PlayerFrame(85, -80, player))
			engine.registerEntity(new PlayerSpells(-85, -65, playerSpells))
			player
		}
		characterEntities += (characterUID -> entity)
		engine.registerEntity(entity)
	}

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		// Builders
		case SkeletonEvent(event) => skeletonManager.receive(event)
		case InstantiateCharacter(characterUID, skeletonUID) => instantiateCharacter(characterUID, skeletonUID)
		case GainSpell(slot, uid) => playerSpells(slot) = Some(skeletonManager.getAs[SpellSkeleton](uid))
		case LoseSpell(slot) => playerSpells(slot) = None
		case DrawShape(shapeUID, shape) =>
			val shapeDrawer = new ShapeDrawer(shape)
			shapeEntities += (shapeUID -> shapeDrawer)
			engine.registerEntity(shapeDrawer)
		case EraseShape(shapeUID) =>
			val shape = shapeEntities(shapeUID)
			shapeEntities -= shapeUID
			engine.unregisterEntity(shape)

		// Camera
		case SetCameraLocation(x, y) => engine.camera.setPoint(x, y)
		case SetCameraFollow(characterUID) =>
			if (characterUID.zero) engine.camera.detach()
			else engine.camera.follow(characterEntities(characterUID))
		case SetCameraSmoothing(smoothing) => engine.camera.setSmoothing(smoothing)
		case SetCameraSpeed(speed) => engine.camera.setSmoothingSpeed(speed)

		// Game start
		case GameStart =>
			App.hidePanels()
			Game.unlock()
	}
}
