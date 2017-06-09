package game.server

import akka.actor.ActorRef
import engine.geometry.Vector2D
import game.UID
import game.protocol.ServerMessage
import game.server.BasicGameImplicits.{CameraApi, EngineApi, UIDApi}
import game.skeleton.Skeleton
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.spells.Spell
import scala.language.implicitConversions

trait BasicGameImplicits { game: BasicGame =>
	/** Implicit reference to self */
	implicit val implicitSelfRef: BasicGame = this

	/** Some quality of life operations on UIDs */
	implicit final class UIDOps(private val uid: UID) extends UIDApi {
		def ! (msg: Any): Unit = {
			game.playersFromUID.get(uid) match {
				case Some(player) => player.actor ! msg
				case None =>
					game.teamFromUID.get(uid) match {
						case Some(team) => for (player <- team.players) player.actor ! msg
						case None => throw new IllegalArgumentException(s"No target found for UID: `$uid`")
					}
			}
		}

		def skeleton: CharacterSkeleton = game.skeletons(uid)
		def latency: Double = game.latencies(uid)
		def spells: Array[Option[SpellSkeleton]] = game.playerSpells(uid)
		def actor: ActorRef = game.playersFromUID(uid).actor
		def team: UID = game.teamForPlayer(uid)

		def color: String = {
			skeletons.get(uid).map(_.color.value).orElse(teamsColor.get(uid)) match {
				case Some(c) => c
				case None => throw new IllegalArgumentException(s"Cannot determine color for UID: $uid")
			}
		}

		def hostile(other: UID): Boolean = game.hostile(uid, other)
		def friendly(other: UID): Boolean = game.friendly(uid, other)

		def gainSpell(slot: Int, spell: Spell): Unit = {
			require(slot >= 0 && slot <= 3, s"Slot must be between 0-3: $slot given")
			val skeleton = createSkeleton(Skeleton.Spell, uid)
			skeleton.spell.value = spell
			spells(slot) = Some(skeleton)
			uid ! ServerMessage.GainSpell(slot, skeleton.uid)
		}

		def loseSpell(slot: Int): Unit = {
			for (skeleton <- spells(slot)) skeleton.collect()
			spells(slot) = None
			uid ! ServerMessage.LoseSpell(slot)
		}

		object engine extends EngineApi {
			def enableInputs(): Unit = uid ! ServerMessage.EnableInputs
			def disableInputs(): Unit = uid ! ServerMessage.DisableInputs
		}

		object camera extends CameraApi {
			def move(x: Double, y: Double): Unit = uid ! ServerMessage.SetCameraLocation(x, y)
			def follow(uid: UID): Unit = uid ! ServerMessage.SetCameraFollow(uid)
			def followSelf(): Unit = uid ! ServerMessage.SetCameraFollow(uid)
			def setSmoothing(smoothing: Boolean): Unit = uid ! ServerMessage.SetCameraSmoothing(smoothing)
			def setSpeed(pps: Double): Unit = uid ! ServerMessage.SetCameraSpeed(pps)
		}
	}

	/** Same as UIDOps but for collections of UIDs */
	implicit final class IterableUIDOps(private val uids: Iterable[UID]) extends UIDApi {
		def ! (msg: Any): Unit = for (uid <- uids) uid ! msg
		def gainSpell(slot: Int, spell: Spell): Unit = for (uid <- uids) uid gainSpell (slot, spell)
		def loseSpell(slot: Int): Unit = for (uid <- uids) uid loseSpell slot

		object engine extends EngineApi {
			def enableInputs(): Unit = for (uid <- uids) uid.engine.enableInputs()
			def disableInputs(): Unit = for (uid <- uids) uid.engine.disableInputs()
		}

		object camera extends CameraApi {
			def move(x: Double, y: Double): Unit = for (uid <- uids) uid.camera.move(x, y)
			def follow(uid: UID): Unit = for (uid <- uids) uid.camera.follow(uid)
			def followSelf(): Unit = for (uid <- uids) uid.camera.followSelf()
			def setSmoothing(smoothing: Boolean): Unit = for (uid <- uids) uid.camera.setSmoothing(smoothing)
			def setSpeed(pps: Double): Unit = for (uid <- uids) uid.camera.setSpeed(pps)
		}
	}
}

object BasicGameImplicits {
	trait UIDApi {
		def ! (msg: Any): Unit

		def gainSpell(slot: Int, spell: Spell): Unit
		def loseSpell(slot: Int): Unit

		val engine: EngineApi
		val camera: CameraApi
	}

	trait EngineApi {
		def enableInputs(): Unit
		def disableInputs(): Unit
	}

	trait CameraApi {
		def move(x: Double, y: Double): Unit
		def follow(uid: UID): Unit
		def followSelf(): Unit
		def setSmoothing(smoothing: Boolean): Unit
		def setSpeed(pps: Double): Unit

		final def move(point: Vector2D): Unit = move(point.x, point.y)
		final def detach(): Unit = follow(UID.zero)
	}
}
