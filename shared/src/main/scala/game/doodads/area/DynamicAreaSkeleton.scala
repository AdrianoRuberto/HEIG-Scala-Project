package game.doodads.area

import engine.geometry.Shape
import game.UID
import game.skeleton.node.SimpleNode
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import utils.Color

class DynamicAreaSkeleton (uid: UID, remotes: Seq[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.DynamicArea, remotes, uid) {

	// Shape to display
	val shape = SimpleNode(null: Shape)

	// Fill settings
	val fill = SimpleNode(value = true)
	val fillColor = SimpleNode(Color(85, 170, 85, 0.1))

	// Stroke settings
	val stroke = SimpleNode(value = true)
	val strokeColor = SimpleNode(Color(85, 170, 85, 0.8))
	val strokeWidth = SimpleNode(2)
}
