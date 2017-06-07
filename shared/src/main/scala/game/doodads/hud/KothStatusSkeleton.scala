package game.doodads.hud

import game.UID
import game.skeleton.node.{InterpolatedNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}

class KothStatusSkeleton (uid: UID, remotes: Seq[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.KothStatus, remotes, uid) {

	val teamA = SimpleNode(UID.zero)
	val teamB = SimpleNode(UID.zero)
	val controlling = SimpleNode(UID.zero)

	val progressA = InterpolatedNode(0.0)
	val progressB = InterpolatedNode(0.0)
	val capture = InterpolatedNode(0.0)

	val contested = SimpleNode(false)
}
