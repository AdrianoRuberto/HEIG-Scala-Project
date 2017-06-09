package game.skeleton.concrete

import engine.geometry.Vector2D
import game.UID
import game.skeleton.node.{InterpolatedNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}

/**
  * A generic Skeleton encoding a point position
  */
class PointSkeleton (uid: UID, remotes: Seq[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Point, uid, remotes) {

	val color = SimpleNode("black")
	val x = InterpolatedNode(0.0)
	val y = InterpolatedNode(0.0)
	def point: Vector2D = Vector2D(x.current, y.current)
}
