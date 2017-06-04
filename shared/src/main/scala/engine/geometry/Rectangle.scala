package engine.geometry

final case class Rectangle (x: Double, y: Double, width: Double, height: Double) extends Shape with ConvexPolygon {
	@inline def boundingBox: Rectangle = this

	lazy val A: Vector2D = Vector2D(x, y)
	lazy val B: Vector2D = Vector2D(x, y + height)
	lazy val C: Vector2D = Vector2D(x + width, y + height)
	lazy val D: Vector2D = Vector2D(x + width, y)

	lazy val vertices: Seq[Vector2D] = Seq(A, B, C, D)

	@inline def left: Double = x
	@inline def right: Double = x + width
	@inline def top: Double = y
	@inline def bottom: Double = y + height

	def contains(point: Vector2D): Boolean = {
		point.x >= left && point.x <= right && point.y >= top && point.y <= bottom
	}

	def contains(shape: Shape): Boolean = shape match {
		case r: Rectangle => r.left >= left && r.right <= right && r.top >= top && r.bottom <= bottom
		case c: Circle => contains(c.boundingBox)
		case cp: ConvexPolygon => cp.vertices.forall(this.contains)
	}

	def intersect(shape: Shape): Boolean = shape match {
		case r: Rectangle => left <= r.right && r.left <= right && top <= r.bottom && r.top <= bottom
		case c: Circle => g.intersect(this, c)
		case cp: ConvexPolygon => g.intersect(this, cp)
	}

	def scale(k: Double): Rectangle = Rectangle(x * k, y * k, width * k, height * k)
	def translate(dx: Double, dy: Double): Rectangle = Rectangle(x + dx, y + dy, width, height)
}
