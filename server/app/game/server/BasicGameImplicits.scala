package game.server

import akka.actor.ActorRef
import game.UID
import game.protocol.ServerMessage
import game.skeleton.Skeleton
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.spells.Spell
import scala.language.implicitConversions

trait BasicGameImplicits { game: BasicGame =>
	/** Some quality of life operations on UIDs */
	implicit final class UIDOps(private val uid: UID) {
		@inline def ! (msg: Any): Unit = game.actorsFromUID.get(uid) match {
			case Some(ag) => ag ! msg
			case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
		}

		@inline def skeleton: CharacterSkeleton = game.skeletons(uid)
		@inline def latency: Double = game.latencies(uid)
		@inline def spells: Array[Option[SpellSkeleton]] = game.playerSpells(uid)
		@inline def actor: ActorRef = game.playersFromUID(uid).actor
		@inline def team: UID = game.teamForPlayer(uid)

		@inline def color: String = {
			skeletons.get(uid).map(_.color.value).orElse(teamsColor.get(uid)) match {
				case Some(c) => c
				case None => throw new IllegalArgumentException(s"Cannot determine color for UID: $uid")
			}
		}

		@inline def hostile(other: UID): Boolean = game.hostile(uid, other)
		@inline def friendly(other: UID): Boolean = game.friendly(uid, other)

		@inline def gainSpell(slot: Int, spell: Spell): Unit = {
			require(slot >= 0 && slot <= 3, s"Slot must be between 0-3: $slot given")
			val skeleton = createSkeleton(Skeleton.Spell, uid)
			skeleton.spell.value = spell
			spells(slot) = Some(skeleton)
			uid ! ServerMessage.GainSpell(slot, skeleton.uid)
		}

		@inline def loseSpell(slot: Int): Unit = {
			for (skeleton <- spells(slot)) skeleton.collect()
			spells(slot) = None
			uid ! ServerMessage.LoseSpell(slot)
		}

		object engine {
			@inline def enableInputs(): Unit = uid ! ServerMessage.EnableInputs
			@inline def disableInputs(): Unit = uid ! ServerMessage.DisableInputs
		}

		object camera {
			def move(x: Double, y: Double): Unit = uid ! ServerMessage.SetCameraLocation(x, y)
			def follow(uid: UID): Unit = uid ! ServerMessage.SetCameraFollow(uid)
			def followSelf(): Unit = uid ! ServerMessage.SetCameraFollow(uid)
			def detach(): Unit = uid ! ServerMessage.SetCameraFollow(UID.zero)
			def setSmoothing(smoothing: Boolean): Unit = uid ! ServerMessage.SetCameraSmoothing(smoothing)
			def setSpeed(pps: Double): Unit = uid ! ServerMessage.SetCameraSpeed(pps)
		}
	}

	implicit final class TraversableUIDSend(private val uids: TraversableOnce[UID]) {
		@inline def ! (msg: Any): Unit = for (uid <- uids) uid ! msg
	}
}
