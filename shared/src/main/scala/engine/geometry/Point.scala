package engine.geometry

case class Point(x: Double, y: Double) {
	@inline def distance(p: Point): Double = distance(p.x, p.y)
	@inline def distance(u: Double, v: Double): Double = Math.sqrt(squaredDistance(u, v))
	@inline def squaredDistance(p: Point): Double = squaredDistance(p.x, p.y)
	@inline def squaredDistance(u: Double, v: Double): Double = g.squaredDistance(x, y, u, v)
}
