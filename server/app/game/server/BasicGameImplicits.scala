package game.server

import akka.actor.ActorRef
import game.UID
import game.protocol.ServerMessage
import game.skeleton.SkeletonType
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.spells.Spell
import scala.language.implicitConversions

trait BasicGameImplicits { game: BasicGame =>
	/** Some quality of life operations on UIDs */
	implicit final class UIDOps(private val uid: UID) {
		@inline def ! (msg: Any): Unit = game.actors.get(uid) match {
			case Some(ag) => ag ! msg
			case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
		}

		@inline def skeleton: CharacterSkeleton = game.skeletons(uid)
		@inline def latency: Double = game.latencies(uid)
		@inline def spells: Array[Option[SpellSkeleton]] = game.spells(uid)
		@inline def actor: ActorRef = game.players(uid).actor
		@inline def team: UID = game.playersTeam(uid)

		@inline def hostile(other: UID): Boolean = game.hostile(uid, other)

		@inline def gainSpell(slot: Int, spell: Spell): Unit = {
			require(slot >= 0 && slot <= 3, s"Slot must be between 0-3: $slot given")
			val skeleton = createSkeleton(SkeletonType.Spell, uid)
			skeleton.spell.value = spell
			spells(slot) = Some(skeleton)
			uid ! ServerMessage.GainSpell(slot, skeleton.uid)
		}

		@inline def loseSpell(slot: Int): Unit = {
			for (skeleton <- spells(slot)) skeleton.collect()
			spells(slot) = None
			uid ! ServerMessage.LoseSpell(slot)
		}
	}

	implicit final class TraversableUIDSend(private val uids: TraversableOnce[UID]) {
		@inline def ! (msg: Any): Unit = for (uid <- uids) uid ! msg
	}
}
