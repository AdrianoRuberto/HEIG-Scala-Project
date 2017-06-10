package game.doodads.hud

import game.UID
import game.skeleton.node.{InterpolatedNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}

case class OvertimeSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Overtime) {

	val enabled = SimpleNode(false)
	val left = InterpolatedNode(100)
}
