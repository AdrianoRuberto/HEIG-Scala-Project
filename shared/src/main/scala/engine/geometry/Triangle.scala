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
	@inline def intersect(s: Segment): Boolean = g.intersect(s, this)
}
