package game.doodads.area

import engine.geometry.Shape
import game.UID
import game.skeleton.node.SimpleNode
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import utils.Color

case class DynamicAreaSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.DynamicArea) {

	// Shape to display
	val shape = SimpleNode(null: Shape)

	// Fill settings
	val fill = SimpleNode(value = true)
	val fillColor = SimpleNode(Color(85, 170, 85, 0.1))

	// Stroke settings
	val stroke = SimpleNode(value = true)
	val strokeColor = SimpleNode(Color(85, 170, 85, 0.8))
	val strokeWidth = SimpleNode(2)

	// Set both fill and stroke color with default alpha values
	def setColor(base: Color): Unit = {
		fillColor.value = base.copy(alpha = 0.1)
		strokeColor.value = base.copy(alpha = 0.8)
	}
}
