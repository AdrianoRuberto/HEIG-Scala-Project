package game.server

import akka.actor.ActorRef
import engine.geometry.{Shape, Vector2D}
import game.UID
import game.doodads.Doodad
import game.protocol.ServerMessage
import game.server.actors.Matchmaker
import game.skeleton.SkeletonManager
import game.skeleton.core.{CharacterSkeleton, SpellSkeleton}
import scala.concurrent.duration._

abstract class BasicBot(name: String) extends BasicActor(name) {
	import context._

	private var gameRef: ActorRef = _
	def game: ActorRef = gameRef

	val skeletons = new SkeletonManager

	var characters: Map[UID, CharacterSkeleton] = Map.empty
	var walls: Map[UID, Shape] = Map.empty
	var doodads: Map[UID, Doodad] = Map.empty
	val spells: Array[Option[SpellSkeleton]] = Array.ofDim(4)

	var botUID: UID = UID.zero
	var bot: CharacterSkeleton = null
	var enabled = false

	context.system.scheduler.scheduleOnce(40.millis, self, BasicBot.Tick) // 25 Hz ticks

	private val spawnTime = System.nanoTime()
	private var lastTick = time
	def time: Double = (System.nanoTime() - spawnTime) / 1000000.0

	private var moving = false
	private var destination: Vector2D = Vector2D(0, 0)

	final def receive: Receive = {
		case BasicBot.Tick =>
			context.system.scheduler.scheduleOnce(40.millis, self, BasicBot.Tick)
			if (enabled) {
				val now = time
				val dt = now - lastTick
				lastTick = now
				tick(dt)
			}

		case ServerMessage.GameFound(mode, team, me, warmup) =>
			botUID = me
			sender() ! Matchmaker.Ready

		case Matchmaker.Bind(actor) => gameRef = actor
		case ServerMessage.SkeletonEvent(event) => skeletons.receive(event)
		case ServerMessage.CreateWall(uid, shape) => walls += (uid -> shape)
		case ServerMessage.RemoveWall(uid) => walls -= uid

		case ServerMessage.CreateDoodad(uid, doodad) =>
			doodads += (uid -> doodad)
			doodadCreated.applyOrElse(doodad, BasicBot.dummyHandler)

		case ServerMessage.RemoveDoodad(uid) =>
			doodadRemoved.applyOrElse(doodads(uid), BasicBot.dummyHandler)
			doodads -= uid

		case ServerMessage.GainSpell(slot, skeletonUID) =>
			spells(slot) = Some(skeletons.getAs[SpellSkeleton](skeletonUID))

		case ServerMessage.LoseSpell(slot) =>
			spells(slot) = None

		case ServerMessage.InstantiateCharacter(uid, skeletonUID) =>
			val skeleton = skeletons.getAs[CharacterSkeleton](skeletonUID)
			characters += (uid -> skeleton)
			if (uid == botUID) bot = skeleton

		case ServerMessage.GameStart =>
			enabled = true
			start()

		case ServerMessage.DisableInputs => enabled = false
		case ServerMessage.EnableInputs => enabled = true

		case _: ServerMessage.CameraMessage => // ignore
		case msg => warn(s"Received unknown message: $msg")
	}

	def start(): Unit = ()
	def tick(dt: Double): Unit = ()

	type DoodadHandler = PartialFunction[Doodad, Unit]
	def doodadCreated: DoodadHandler = {case _ =>}
	def doodadRemoved: DoodadHandler = {case _ =>}
}

object BasicBot {
	case object Tick
	val dummyHandler: Any => Unit = _ => ()
}
