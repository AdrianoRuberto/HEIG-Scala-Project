package game.skeleton.concrete

import engine.geometry.Shape
import game.UID
import game.skeleton.node.SimpleNode
import game.skeleton.{AbstractSkeleton, RemoteManager, SkeletonType}

class DynamicAreaSkeleton (uid: UID, remotes: Seq[RemoteManager])
	extends AbstractSkeleton(SkeletonType.DynamicArea, remotes, uid) {

	val shape = SimpleNode(null: Shape)

	val fill = SimpleNode(true)
	val fillColor = SimpleNode("rgba(85, 170, 85, 0.1)")

	val stroke = SimpleNode(true)
	val strokeColor = SimpleNode("rgba(85, 170, 85, 0.8)")
	val strokeWidth = SimpleNode(1)
}
