package engine.geometry

import java.lang.Math._

case class Vector2D(x: Double, y: Double) {
	// Alias this as `a` for formulas
	a =>

	def + (b: Vector2D): Vector2D = Vector2D(a.x + b.x, a.y + b.y)
	def - (b: Vector2D): Vector2D = Vector2D(a.x - b.x, a.y - b.y)

	def * (k: Double): Vector2D = Vector2D(a.x * k, a.y * k)
	def / (k: Double): Vector2D = Vector2D(a.x / k, a.y / k)

	def unary_-(): Vector2D = Vector2D(-a.x, -a.y)

	def * (b: Vector2D): Double = a.x * b.x + a.y * b.y
	def cross(b: Vector2D): Double = a.x * b.y - a.y * b.x

	def norm: Double = sqrt(a * a)
	def orthogonal: Vector2D = Vector2D(-a.y, a.x)
	def normalized: Vector2D = a / norm

	def project(b: Vector2D): Vector2D = (a * b) / (b * b) * b
	def reject(b: Vector2D): Vector2D = a - (a project b)

	/** Distance operator, return the norm of the vector `(b - a)` */
	def <-> (b: Vector2D): Double = (b - a).norm

	def isZero: Boolean = a.x == 0.0 && a.y == 0.0
	def notZero: Boolean = a.x != 0.0 || a.y != 0.0
}

object Vector2D {
	val zero = Vector2D(0.0, 0.0)

	implicit final class CommutativeVectorOps(private val k: Double) extends AnyVal {
		@inline def * (a: Vector2D): Vector2D = a * k
		@inline def / (a: Vector2D): Vector2D = a / k
	}
}
