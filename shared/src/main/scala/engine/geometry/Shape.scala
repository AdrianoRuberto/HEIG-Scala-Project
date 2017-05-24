package engine.geometry

import engine.geometry.QuadTree.Bounded

trait Shape {
	def boundingBox: Rectangle

	def contains(x: Double, y: Double): Boolean
	@inline final def contains(point: Point): Boolean = contains(point.x, point.y)

	def contains(r: Rectangle): Boolean
	def contains(c: Circle): Boolean

	@inline final def contains(shape: Shape): Boolean = shape match {
		case r: Rectangle => contains(r)
		case c: Circle => contains(c)
	}

	def intersect(r: Rectangle): Boolean
	def intersect(c: Circle): Boolean

	@inline final def intersect(shape: Shape): Boolean = shape match {
		case r: Rectangle => intersect(r)
		case c: Circle => intersect(c)
	}

	@inline final def disjoint(r: Rectangle): Boolean = !(this intersect r)
	@inline final def disjoint(c: Circle): Boolean = !(this intersect c)
	@inline final def disjoint(shape: Shape): Boolean = shape match {
		case r: Rectangle => disjoint(r)
		case c: Circle => disjoint(c)
	}
}

object Shape {
	implicit object ShapeIsBounded extends Bounded[Shape] {
		def boundingBox(shape: Shape): Rectangle = shape.boundingBox
	}
}
