package engine.geometry

case class Segment(x1: Double, y1: Double, x2: Double, y2: Double) extends Shape {
	val A: Double = y2 - y1
	val B: Double = x1 - x2
	val C: Double = A * x1 + B * y1

	@inline def length: Double = math.sqrt(g.squaredDistance(x1, y1, x2, y2))

	def intersect(s: Segment): Boolean = g.intersect(this, s)
	def intersect(r: Rectangle): Boolean = g.intersect(this, r)
	def intersect(c: Circle): Boolean = g.intersect(this, c)
	def intersect(t: Triangle): Boolean = g.intersect(this, t)

	def boundingBox: Rectangle = Rectangle(x1 min x2, y1 min y2, Math.abs(x2 - x1), Math.abs(y2 - y1))
	def contains(x: Double, y: Double): Boolean = Vector(x - x1, y - y1).cross(Vector(x2 - x, y2 - y)) == 0
	def contains(c: Circle): Boolean = c.radius == 0 && contains(c.x, c.y)

	def scale (k: Double): Shape = Segment(x1 * k, y1 * k, x2 * k, y2 * k)
}
