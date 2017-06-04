package engine.geometry

case class Circle(x: Double, y: Double, radius: Double) extends Shape {
	lazy val boundingBox: Rectangle = Rectangle(x - radius, y - radius, radius * 2, radius * 2)

	lazy val center: Vector2D = Vector2D(x, y)

	def contains(point: Vector2D): Boolean = center <-> point <= radius

	def contains(shape: Shape): Boolean = shape match {
		case c: Circle => (center <-> c.center) + c.radius <= radius
		case cp: ConvexPolygon => cp.vertices.forall(this.contains)
	}

	def intersect(shape: Shape): Boolean = shape match {
		case c: Circle => c.center <-> center <= radius + c.radius
		case cp: ConvexPolygon => g.intersect(cp, this)
	}

	def translate(dx: Double, dy: Double): Circle = Circle(x + dx, y + dy, radius)
	def scale(k: Double): Circle = Circle(x * k, y * k, radius * k)
}
