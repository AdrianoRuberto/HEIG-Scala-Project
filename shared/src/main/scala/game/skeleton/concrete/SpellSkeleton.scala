package game.skeleton.concrete

import boopickle.Default._
import game.UID
import game.skeleton.node.{CooldownNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManager, SkeletonType}
import game.spells.Spell

class SpellSkeleton (uid: UID, remotes: Seq[RemoteManager])
	extends AbstractSkeleton(SkeletonType.Spell, remotes, uid) {

	val spell = SimpleNode(null: Spell)
	val cooldown = new CooldownNode
	val activated = SimpleNode(false)
}
