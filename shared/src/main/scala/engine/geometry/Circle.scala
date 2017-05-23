package engine.geometry

case class Circle(center: Point, radius: Double) extends Shape {
	@inline def north: Point = Point(center.x, center.y - radius)
	@inline def south: Point = Point(center.x, center.y + radius)
	@inline def west: Point = Point(center.x - radius, center.y)
	@inline def east: Point = Point(center.x + radius, center.y)

	lazy val cardinals = Seq(north, west, south, east)

	def boundingBox: Rectangle = Rectangle(Point(center.x - radius, center.y - radius), Size(radius * 2, radius * 2))

	def contains(point: Point): Boolean = (center distance point) <= radius

	def contains(shape: Shape): Boolean = shape match {
		case r: Rectangle => r.corners.forall(contains)
		case c: Circle => c.cardinals.forall(contains)
	}

	def intersect(shape: Shape): Boolean = shape match {
		case r: Rectangle => r intersect this
		case c: Circle => (center distance c.center) <= (radius + c.radius)
	}
}

