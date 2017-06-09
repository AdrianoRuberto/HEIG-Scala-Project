package game.skeleton.concrete

import game.UID
import game.skeleton.node.{CooldownNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import game.spells.Spell

class SpellSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Spell, uid, remotes) {

	val spell = SimpleNode(null: Spell)
	val cooldown = new CooldownNode
	val activated = SimpleNode(value = false)
}
