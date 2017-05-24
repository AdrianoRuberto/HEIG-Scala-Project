package engine.geometry

case class Circle(x: Double, y: Double, radius: Double) extends Shape {
	@inline def center: Point = Point(x, y)

	lazy val squaredRadius: Double = radius * radius
	lazy val boundingBox: Rectangle = Rectangle(x - radius, y - radius, radius * 2, radius * 2)

	/** A circle contains a point if the distance of this point from its center is smaller than the radius */
	@inline def contains(u: Double, v: Double): Boolean = g.squaredDistance(x, y, u, v) <= squaredRadius

	/** A circle contains a rectangle if every corner of this rectangle are contained in the circle */
	@inline def contains(r: Rectangle): Boolean = contains(r.left, r.top) && contains(r.right, r.bottom) &&
	                                              contains(r.right, r.top) && contains(r.left, r.bottom)

	/**
	  * A circle contains another circle if the distance of its center
	  * plus its radius is lower than the radius of the former circle
	  */
	@inline def contains(c: Circle): Boolean = g.squaredDistance(x, y, c.x, c.y) + c.squaredRadius <= squaredRadius

	@inline def intersect(r: Rectangle): Boolean = g.intersect(r, this)
	@inline def intersect(c: Circle): Boolean = g.intersect(this, c)
}

object Circle {
	@inline def apply(center: Point, r: Double): Circle = Circle(center.x, center.y, r)
}
