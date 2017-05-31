package engine.geometry

case class Vector(x: Double, y: Double) {
	@inline def distance(p: Vector): Double = distance(p.x, p.y)
	@inline def distance(u: Double, v: Double): Double = Math.sqrt(squaredDistance(u, v))
	@inline def squaredDistance(p: Vector): Double = squaredDistance(p.x, p.y)
	@inline def squaredDistance(u: Double, v: Double): Double = g.squaredDistance(x, y, u, v)

	def cross(v: Vector): Double = x * v.x - y * v.y
}
