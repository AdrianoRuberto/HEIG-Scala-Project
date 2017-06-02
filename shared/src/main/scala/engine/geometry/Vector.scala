package engine.geometry

case class Vector(x: Double, y: Double) {
	def + (v: Vector): Vector = Vector(x + v.x, y + v.y)
	def - (v: Vector): Vector = Vector(x - v.x, y - v.y)

	def unary_-(): Vector = Vector(-x, -y)

	def * (v: Vector): Double = x * v.x + y * v.y

	def * (k: Double): Vector = Vector(x * k, y * k)
	def / (k: Double): Vector = Vector(x / k, y / k)

	def cross(v: Vector): Double = x * v.y - y * v.x
	def norm: Double = math.sqrt(x * x + y * y)
	def project(v: Vector): Vector = v * (this * v)
	def orthogonal: Vector = Vector(-y, x)
	def normalized: Vector = this / norm

	@inline def squaredDistance(v: Vector): Double = squaredDistance(v.x, v.y)
	@inline def squaredDistance(u: Double, v: Double): Double = g.squaredDistance(x, y, u, v)
	@inline def distance(v: Vector): Double = distance(v.x, v.y)
	@inline def distance(u: Double, v: Double): Double = Math.sqrt(squaredDistance(u, v))

	def rotate(rad: Double): Vector = Vector(x * math.cos(rad) - y * math.sin(rad), x * math.sin(rad) + y * math.cos(rad))

	def isZero: Boolean = x == 0 && y == 0
	def notZero: Boolean = x != 0 || y != 0
}
