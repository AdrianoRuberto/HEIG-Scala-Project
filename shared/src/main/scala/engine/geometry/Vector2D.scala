package engine.geometry

case class Vector2D(x: Double, y: Double) {
	def + (v: Vector2D): Vector2D = Vector2D(x + v.x, y + v.y)
	def - (v: Vector2D): Vector2D = Vector2D(x - v.x, y - v.y)

	def unary_-(): Vector2D = Vector2D(-x, -y)

	def * (v: Vector2D): Double = x * v.x + y * v.y

	def * (k: Double): Vector2D = Vector2D(x * k, y * k)
	def / (k: Double): Vector2D = Vector2D(x / k, y / k)

	def cross(v: Vector2D): Double = x * v.y - y * v.x
	def norm: Double = math.sqrt(x * x + y * y)
	def project(v: Vector2D): Vector2D = v * (this * v)
	def orthogonal: Vector2D = Vector2D(-y, x)
	def normalized: Vector2D = this / norm

	@inline def squaredDistance(v: Vector2D): Double = squaredDistance(v.x, v.y)
	@inline def squaredDistance(u: Double, v: Double): Double = g.squaredDistance(x, y, u, v)
	@inline def distance(v: Vector2D): Double = distance(v.x, v.y)
	@inline def distance(u: Double, v: Double): Double = Math.sqrt(squaredDistance(u, v))

	def rotate(rad: Double): Vector2D = Vector2D(x * math.cos(rad) - y * math.sin(rad), x * math.sin(rad) + y * math.cos(rad))

	def isZero: Boolean = x == 0 && y == 0
	def notZero: Boolean = x != 0 || y != 0
}

object Vector2D {
	val zero = Vector2D(0, 0)
}
