package engine
package entity
package feature

import engine.geometry.Rectangle
import engine.quadtree.{Bounded, BoundingBox}

trait Position extends Entity {
	def boundingBox: Rectangle
	def positionIsAbsolute: Boolean = false
}

object Position {
	implicit object PositionIsBounded extends Bounded[Position] {
		def boundingBox(obj: Position): BoundingBox = BoundingBox(obj.boundingBox)
	}
}
