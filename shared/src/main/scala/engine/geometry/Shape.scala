package engine.geometry

import boopickle.Default._
import engine.quadtree.{Bounded, BoundingBox}

trait Shape {
	def boundingBox: Rectangle

	def contains(x: Double, y: Double): Boolean
	@inline final def contains(point: Vector): Boolean = contains(point.x, point.y)

	def contains(r: Rectangle): Boolean = contains(r.left, r.top) && contains(r.right, r.bottom) &&
	                                      contains(r.right, r.top) && contains(r.left, r.bottom)
	def contains(c: Circle): Boolean
	def contains(t: Triangle): Boolean = contains(t.ax, t.ay) && contains(t.bx, t.by) && contains(t.cx, t.cy)

	@inline final def contains(shape: Shape): Boolean = shape match {
		case r: Rectangle => contains(r)
		case c: Circle => contains(c)
		case t: Triangle => contains(t)
	}

	def intersect(r: Rectangle): Boolean
	def intersect(c: Circle): Boolean
	def intersect(t: Triangle): Boolean

	@inline final def intersect(shape: Shape): Boolean = shape match {
		case r: Rectangle => intersect(r)
		case c: Circle => intersect(c)
		case t: Triangle => intersect(t)
	}

	@inline final def disjoint(r: Rectangle): Boolean = !(this intersect r)
	@inline final def disjoint(c: Circle): Boolean = !(this intersect c)
	@inline final def disjoint(t: Triangle): Boolean = !(this intersect t)

	@inline final def disjoint(shape: Shape): Boolean = shape match {
		case r: Rectangle => disjoint(r)
		case c: Circle => disjoint(c)
	}
}

object Shape {
	implicit object ShapeIsBounded extends Bounded[Shape] {
		def boundingBox(shape: Shape): BoundingBox = shape.boundingBox
	}
	implicit val pickler: Pickler[Shape] = compositePickler[Shape].addConcreteType[Triangle].addConcreteType[Rectangle].addConcreteType[Circle]
}
