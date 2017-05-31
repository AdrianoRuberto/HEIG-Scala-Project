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
	def contains(s: Segment): Boolean = contains(s.x1, s.y1) && contains(s.x2, s.y2)

	@inline final def contains(shape: Shape): Boolean = shape match {
		case r: Rectangle => contains(r)
		case c: Circle => contains(c)
		case t: Triangle => contains(t)
		case s: Segment => contains(s)
	}

	def intersect(r: Rectangle): Boolean
	def intersect(c: Circle): Boolean
	def intersect(t: Triangle): Boolean
	def intersect(s: Segment): Boolean

	@inline final def intersect(shape: Shape): Boolean = shape match {
		case r: Rectangle => intersect(r)
		case c: Circle => intersect(c)
		case t: Triangle => intersect(t)
		case s: Segment => intersect(s)
	}

	@inline final def disjoint(shape: Shape): Boolean = !(this intersect shape)
}

object Shape {
	implicit object ShapeIsBounded extends Bounded[Shape] {
		def boundingBox(shape: Shape): BoundingBox = shape.boundingBox
	}
	implicit val pickler: Pickler[Shape] = compositePickler[Shape]
		.addConcreteType[Triangle]
		.addConcreteType[Rectangle]
		.addConcreteType[Circle]
		.addConcreteType[Segment]
}
