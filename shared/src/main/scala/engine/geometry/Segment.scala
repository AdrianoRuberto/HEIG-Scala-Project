package engine.geometry

import java.lang.Math._

case class Segment(ax: Double, ay: Double, bx: Double, by: Double) extends Shape with ConvexPolygon {
	lazy val boundingBox: Rectangle = Rectangle(ax min bx, ay min by, abs(bx - ax), abs(by - ay))

	lazy val A: Vector2D = Vector2D(ax, ay)
	lazy val B: Vector2D = Vector2D(bx, by)
	def vertices: Seq[Vector2D] = Seq(A, B)

	lazy val AB: Vector2D = B - A

	def contains(c: Vector2D): Boolean = c.x >= (ax min bx) && c.x <= (ax max bx) && (AB cross c - A) == 0

	def contains(shape: Shape): Boolean = shape match {
		case c: Circle => c.radius == 0 && contains(c.center)
		case cp: ConvexPolygon => cp.vertices.forall(this.contains)
	}

	def intersect(shape: Shape): Boolean = shape match {
		case c: Circle => g.intersect(this, c)
		case cp: ConvexPolygon => g.intersect(this, cp)
	}

	def translate(dx: Double, dy: Double): Segment = Segment(ax + dx, ay + dy, bx + dx, by + dy)
	def scale(k: Double): Segment = Segment(ax * k, ay * k, bx * k, by * k)
}

object Segment {
	def apply(from: Vector2D, to: Vector2D): Segment = Segment(from.x, from.y, to.x, to.y)
}
