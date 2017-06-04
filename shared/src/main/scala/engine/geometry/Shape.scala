package engine.geometry

import boopickle.Default._

trait Shape {
	def boundingBox: Rectangle

	def contains(point: Vector2D): Boolean
	def contains(shape: Shape): Boolean
	def intersect(shape: Shape): Boolean

	final def disjoint(shape: Shape): Boolean = !(this intersect shape)

	def translate(dx: Double, dy: Double): Shape
	def scale(k: Double): Shape

	def colored(color: String): ColoredShape = ColoredShape(this, color)
}

object Shape {
	implicit val pickler: Pickler[Shape] =
		compositePickler[Shape]
			.addConcreteType[Segment]
			.addConcreteType[Triangle]
			.addConcreteType[Rectangle]
			.addConcreteType[Circle]
}
