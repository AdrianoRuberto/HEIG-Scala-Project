package game.doodads.hud

import game.UID
import game.skeleton.node.{InterpolatedNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import utils.Color

case class CtfStatusSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.CtfStatus) {

	val teamA = SimpleNode(UID.zero)
	val teamB = SimpleNode(UID.zero)

	val colorA = SimpleNode(Color.black)
	val colorB = SimpleNode(Color.black)

	val scoreA = SimpleNode(0)
	val scoreB = SimpleNode(0)

	val controllingA = SimpleNode(true)
	val controllingB = SimpleNode(true)

	val timer = InterpolatedNode(0.0)
}
