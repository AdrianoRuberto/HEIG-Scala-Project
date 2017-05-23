package engine.geometry

trait Shape {
	def boundingBox: Rectangle
	def contains(point: Point): Boolean
	def contains(shape: Shape): Boolean
	def intersect(shape: Shape): Boolean
	final def disjoint(shape: Shape): Boolean = !(this intersect shape)
}
