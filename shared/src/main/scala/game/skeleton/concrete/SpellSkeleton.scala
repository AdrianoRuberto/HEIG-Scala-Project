package game.skeleton.concrete

import boopickle.Default._
import game.UID
import game.skeleton.node.{CooldownNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import game.spells.Spell

class SpellSkeleton (uid: UID, remotes: Seq[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Spell, remotes, uid) {

	val spell = SimpleNode(null: Spell)
	val cooldown = new CooldownNode
	val activated = SimpleNode(value = false)
}
