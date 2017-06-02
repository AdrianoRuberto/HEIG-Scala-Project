package game.skeleton.concrete

import boopickle.Default._
import game.UID
import game.protocol.enums.{SkeletonType, Spell}
import game.skeleton.node.{CooldownNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, Transmitter}

class SpellSkeleton (uid: UID = UID.next)
                    (implicit receiver: Transmitter = Transmitter.NoTransmitter)
	extends AbstractSkeleton(SkeletonType.Spell, uid) {

	val spell = SimpleNode(null: Spell)
	val cooldown = new CooldownNode
	val activated = SimpleNode(false)
}
