package engine.geometry

import boopickle.DefaultBasic._

case class Rectangle(corner: Point, size: Size) extends Shape {
	@inline def topLeft: Point = corner
	@inline def topRight: Point = Point(corner.x + size.width, corner.y)
	@inline def bottomLeft: Point = Point(corner.x, corner.y + size.height)
	@inline def bottomRight: Point = Point(corner.x + size.width, corner.y + size.height)

	lazy val corners: Seq[Point] = Seq(topLeft, topRight, bottomLeft, bottomRight)

	def boundingBox: Rectangle = this

	def contains(point: Point): Boolean = {
		if (point.x < topLeft.x || point.x > bottomRight.x) false
		if (point.y < topLeft.y || point.y > bottomRight.y) false
		else true
	}

	def contains(shape: Shape): Boolean = shape match {
		case r: Rectangle => (this contains r.topLeft) && (this contains r.bottomRight)
		case c: Circle => this contains c.boundingBox
	}

	def intersect(shape: Shape): Boolean = shape match {
		case r: Rectangle =>
			if (topLeft.x > r.bottomRight.x || r.topLeft.x > bottomRight.x) false
			else if (topLeft.y > r.bottomRight.y || r.topLeft.y > bottomRight.y) false
			else true

		case c: Circle =>
			if (this disjoint c.boundingBox) false
			else if (this contains c) true
			else c.cardinals.forall(contains)
	}
}

object Rectangle {
	def apply(x: Double, y: Double, w: Double, h: Double): Rectangle = apply(Point(x, y), Size(w, h))
	implicit val pickler: Pickler[Rectangle] = PicklerGenerator.generatePickler[Rectangle]
}
