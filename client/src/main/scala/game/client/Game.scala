package game.client

import engine.Engine
import engine.entity.Entity
import engine.geometry.Shape
import game.client.entities.{Character, DebugStats, Player, PlayerFrame, PlayerSpells}
import game.doodads.DoodadFactory
import game.protocol.ServerMessage._
import game.protocol.{ClientMessage, ServerMessage}
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.skeleton.{AbstractSkeleton, SkeletonManager}
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

	private var characters: Map[UID, Entity] = Map.empty
	private var doodads: Map[UID, Entity] = Map.empty
	private var walls: Map[UID, Shape] = Map.empty

	private var teams: Seq[TeamInfo] = Nil
	private var playerUID: UID = UID.zero

	private var player: CharacterSkeleton = null
	private val spells: Array[Option[SpellSkeleton]] = Array.fill(4)(None)

	def setup(): Unit = {
		dom.window.on(Event.Resize) { _ => resizeCanvas() }
		resizeCanvas()
		engine.setup()
		engine.keyboard.registerKey("alt-s")(toggleDebugStats())
		engine.keyboard.registerKey("e", spellKeyDown(1), spellKeyUp(1))
		engine.keyboard.registerKey("q", spellKeyDown(2), spellKeyUp(2))
		engine.keyboard.registerKey("shift", spellKeyDown(3), spellKeyUp(3))
	}

	private[client] def spellKeyDown(slot: Int)(): Unit = spells(slot) match {
		case Some(skeleton) =>
			if (skeleton.cooldown.ready && (player == null || skeleton.spell.value.cost.forall(player.energy.current >= _))) {
				skeleton.activated.value = true
				Server ! ClientMessage.SpellCast(slot, engine.mouse.point)
			}
		case _ => // Ignore
	}

	private[client] def spellKeyUp(slot: Int)(): Unit = spells(slot) match {
		case Some(skeleton) => if (skeleton.activated.value) Server ! ClientMessage.SpellCancel(slot)
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
	}

	def reset(): Unit = {
		engine.unregisterAllEntities()
		engine.camera.setSmoothing(false)
		skeletonManager.clear()
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()
	def stop(): Unit = engine.stop()

	def getSkeleton[T <: AbstractSkeleton](uid: UID): T = skeletonManager.getAs[T](uid)

	private def toggleDebugStats(): Unit = {
		if (debugStatsShown) engine.unregisterEntity(debugStatsEntity)
		else engine.registerEntity(debugStatsEntity)
		debugStatsShown.toggle()
	}

	private def instantiateCharacter(characterUID: UID, skeletonUID: UID): Unit = {
		val skeleton = skeletonManager.getAs[CharacterSkeleton](skeletonUID)
		val entity = if (characterUID != playerUID) new Character(skeleton) else {
			player = skeleton
			val e = new Player(skeleton, walls.values)
			engine.registerEntity(new PlayerFrame(85, -80, skeleton))
			engine.registerEntity(new PlayerSpells(-85, -65, skeleton, spells))
			e
		}
		characters += (characterUID -> entity)
		engine.registerEntity(entity)
	}

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		// Skeleton
		case SkeletonEvent(event) => skeletonManager.receive(event)
		case InstantiateCharacter(characterUID, skeletonUID) => instantiateCharacter(characterUID, skeletonUID)

		// Spells
		case GainSpell(slot, uid) => spells(slot) = Some(skeletonManager.getAs[SpellSkeleton](uid))
		case LoseSpell(slot) => spells(slot) = None

		// Doodads
		case CreateDoodad(uid, doodad) =>
			val entity = DoodadFactory.create(doodad)
			engine.registerEntity(entity)
			doodads += (uid -> entity)

		case RemoveDoodad(uid) =>
			doodads.get(uid) match {
				case Some(entity) =>
					entity.unregister()
					doodads -= uid
				case None =>
					dom.console.warn(s"Attempted to remove unknown doodad: $uid")
			}

		// Walls
		case CreateWall(uid, shape) => walls += (uid -> shape)
		case RemoveWall(uid) => walls -= uid

		// Inputs
		case EnableInputs => engine.unlock()
		case DisableInputs => engine.lock()

		// Camera
		case SetCameraLocation(x, y) => engine.camera.setPoint(x, y)
		case SetCameraFollow(characterUID) =>
			if (characterUID.zero) engine.camera.detach()
			else engine.camera.follow(characters(characterUID))
		case SetCameraSmoothing(smoothing) => engine.camera.setSmoothing(smoothing)
		case SetCameraSpeed(speed) => engine.camera.setSmoothingSpeed(speed)

		// Game start
		case GameStart =>
			App.hidePanels()
			Game.unlock()
	}
}
