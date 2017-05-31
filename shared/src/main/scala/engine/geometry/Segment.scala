package engine.geometry

case class Segment(x1: Double, y1: Double, x2: Double, y2: Double) extends Shape {
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

			(x1 min x2) <= x && x <= (x1 max x2) &&
			(y1 min y2) <= y && y <= (y1 max y2)
		}
	}

	def boundingBox: Rectangle = Rectangle(x1 min x2, y1 min y2, Math.abs(x2 - x1), Math.abs(y2 - y1))
	def contains(x: Double, y: Double): Boolean = ???
	def contains(c: Circle): Boolean = ???
	def intersect(r: Rectangle): Boolean = ???
	def intersect(c: Circle): Boolean = ???
	def intersect(t: Triangle): Boolean = ???
}
