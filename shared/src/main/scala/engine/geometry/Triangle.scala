package engine.geometry

final case class Triangle(ax: Double, ay: Double,
                          bx: Double, by: Double,
                          cx: Double, cy: Double) extends Shape with ConvexPolygon {

	lazy val boundingBox: Rectangle = {
		val xmin = ax min bx min cx
		val xmax = ax max bx max cx
		val ymin = ay min by min cy
		val ymax = ay max by max cy
		Rectangle(xmin, ymin, xmax - xmin, ymax - ymin)
	}

	lazy val A: Vector2D = Vector2D(ax, ay)
	lazy val B: Vector2D = Vector2D(bx, by)
	lazy val C: Vector2D = Vector2D(cx, cy)
	lazy val vertices: Seq[Vector2D] = Seq(A, B, C)

	lazy val AB: Vector2D = B - A
	lazy val AC: Vector2D = C - A
	lazy val BC: Vector2D = C - B

	def contains(point: Vector2D): Boolean = ???
	def contains(shape: Shape): Boolean = ???

	def intersect(shape: Shape): Boolean = shape match {
		case c: Circle => g.intersect(this, c)
		case cp: ConvexPolygon => g.intersect(this, cp)
	}

	def translate(dx: Double, dy: Double): Triangle = Triangle(ax + dx, ay + dy, bx + dx, by + dy, cx + dx, cy + dy)
	def scale(k: Double): Triangle = Triangle(ax * k, ay * k, bx * k, by * k, cx *k, cy *k)
}
