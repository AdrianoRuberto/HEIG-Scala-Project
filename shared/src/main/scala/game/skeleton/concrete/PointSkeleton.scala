package game.skeleton.concrete

import engine.geometry.Vector2D
import game.UID
import game.skeleton.node.{InterpolatedNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManager, SkeletonType}

class PointSkeleton (uid: UID, remotes: Seq[RemoteManager])
	extends AbstractSkeleton(SkeletonType.Point, remotes, uid) {

	val color = SimpleNode("red")
	val x = InterpolatedNode(0.0)
	val y = InterpolatedNode(0.0)
	def point: Vector2D = Vector2D(x.current, y.current)
}
