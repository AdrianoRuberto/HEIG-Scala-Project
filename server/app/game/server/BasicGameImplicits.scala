package game.server

import game.UID
import game.protocol.ServerMessage
import game.protocol.enums.{SkeletonType, Spell}
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import scala.language.implicitConversions

trait BasicGameImplicits {
	this: BasicGame =>

	/** Some quality of life operations on UIDs */
	implicit final class UIDOps(private val uid: UID) {
		@inline def ! (msg: Any): Unit = actors.get(uid) match {
			case Some(ag) => ag ! msg
			case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
		}

		@inline def skeleton: CharacterSkeleton = BasicGameImplicits.this.skeletons(uid)
		@inline def latency: Double = BasicGameImplicits.this.latencies(uid)
		@inline def spells: Array[Option[SpellSkeleton]] = BasicGameImplicits.this.spells(uid)

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
}
