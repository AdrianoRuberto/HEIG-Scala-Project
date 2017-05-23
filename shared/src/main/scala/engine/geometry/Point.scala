package engine.geometry

case class Point(x: Double, y: Double) {
	def distance(p: Point): Double = {
		val dx = x - p.x
		val dy = y - p.y
		Math.sqrt(dx * dx + dy * dy)
	}
}
