package game.skeleton.core

import game.UID
import game.skeleton.node.{CooldownNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import game.spells.Spell

case class SpellSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Spell) {

	val spell = SimpleNode(null: Spell)
	val cooldown = new CooldownNode
	val activated = SimpleNode(value = false)
}
