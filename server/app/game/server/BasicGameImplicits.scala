package game.server

import game.UID
import game.protocol.ServerMessage
import game.protocol.enums.Spell
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.skeleton.{ManagerEvent, Transmitter}
import scala.language.implicitConversions

trait BasicGameImplicits {
	this: BasicGame =>

	/**
	  * An instance of [[Transmitter]] that send [[ManagerEvent]] to every players of the game.
	  * It is used as implicit parameter during the construction of the [[skeletons]] map.
	  */
	implicit object SkeletonTransmitter extends Transmitter {
		def ! (event: ManagerEvent): Unit = {
			broadcast ! ServerMessage.SkeletonEvent(event)
		}
		def sendLatencyAware (f: (Double) => ManagerEvent): Unit = {
			for ((uid, player) <- players; latency = latencies(uid)) {
				player.actor ! ServerMessage.SkeletonEvent(f(latency))
			}
		}
	}

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
			val skeleton = new SpellSkeleton()
			skeleton.spell.value = spell
			spells(slot) = Some(skeleton)
			uid ! ServerMessage.GainSpell(slot, skeleton.uid)
		}

		@inline def loseSpell(slot: Int): Unit = {
			spells(slot) = None
			uid ! ServerMessage.LoseSpell(slot)
		}
	}
}
