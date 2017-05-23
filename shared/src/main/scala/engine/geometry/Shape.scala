package engine.geometry

import boopickle.DefaultBasic._

trait Shape {
	def boundingBox: Rectangle
	def contains(point: Point): Boolean
	def contains(shape: Shape): Boolean
	def intersect(shape: Shape): Boolean
	final def disjoint(shape: Shape): Boolean = !(this intersect shape)
}

object Shape {
	implicit val pickler: Pickler[Shape] = compositePickler[Shape].addConcreteType[Rectangle].addConcreteType[Circle]
}
