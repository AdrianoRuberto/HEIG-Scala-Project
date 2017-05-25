package engine.geometry

case class Triangle(ax: Double, ay: Double, bx: Double, by: Double, cx: Double, cy: Double) extends Shape {

	@inline def AB: Segment = Segment(ax, ay, bx, by)
	@inline def AC: Segment = Segment(ax, ay, cx, cy)
	@inline def BC: Segment = Segment(bx, by, cx, cy)

	@inline def area: Double = {
		val s = (AB.length + AC.length + BC.length) / 2
		math.sqrt(s * (s - AB.length) * (s - AC.length) * (s - BC.length))
	}

	lazy val height: Double = 2 * area / BC.length

	def boundingBox: Rectangle = {
		@inline def max(a: Double, b: Double, c: Double): Double = math.max(math.max(a, b), c)
		@inline def min(a: Double, b: Double, c: Double): Double = math.min(math.min(a, b), c)

		val left = min(ax, bx, cx)
		val right = max(ax, bx, cx)
		val top = min(ay, by, cy)
		val bot = max(ay, by, cy)

		Rectangle(left, top, right - left, bot - top)
	}

	@inline def contains(x: Double, y: Double): Boolean = {
		val dx = x - cx
		val dy = y - cy
		val det = (by - cy) * (ax - cx) - (cx - bx) * (cy - ay)
		val minD = math.min(det, 0)
		val maxD = math.max(det, 0)
		val a = (by - cy) * dx + (cx - bx) * dy
		val b = (cy - ay) * dx + (ax - cx) * dy
		val c = det - a - b

		(a >= minD && a <= maxD) || (b >= minD && b <= maxD) || (c >= minD && c <= maxD)
	}

	@inline def contains(c: Circle): Boolean = {
		if (!contains(c.center)) false
		else
			c.radius <= Triangle(c.center.x, c.center.y, ax, ay, bx, by).height &&
			c.radius <= Triangle(c.center.x, c.center.y, ax, ay, cx, cy).height &&
			c.radius <= Triangle(c.center.x, c.center.y, bx, by, cx, cy).height
	}

	@inline def intersect(r: Rectangle): Boolean = g.intersect(this, r)
	@inline def intersect(c: Circle): Boolean = g.intersect(this, c)
	@inline def intersect(t: Triangle): Boolean = g.intersect(this, t)
}

object Triangle {
	@inline def apply(A: Point, B: Point, C: Point): Triangle = Triangle(A.x, A.y, B.x, B.y, C.x, C.y)
}

case class Segment(x1: Double, y1: Double, x2: Double, y2: Double) {
	val A: Double = y2 - y1
	val B: Double = x1 - x2
	val C: Double = A * x1 + B * y1

	@inline def length: Double = math.sqrt(g.squaredDistance(x1, y1, x2, y2))

	def intersect(line: Segment): Boolean = {
		val det = A * line.B - line.A * B
		if (det == 0) false
		else {
			val x = (line.B * C - B * line.C) / det
			val y = (A * line.C - line.A * C) / det

			math.min(x1, x2) <= x && x <= math.max(x1, x2) &&
			math.min(y1, y2) <= y && y <= math.max(y1, y2)
		}
	}
}