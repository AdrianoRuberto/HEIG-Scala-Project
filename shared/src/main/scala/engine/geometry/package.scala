package engine

package object geometry {
	private[geometry] object g {
		@inline def intersect(a: Rectangle, b: Rectangle): Boolean = {
			!(a.left > b.right || b.left > a.right || a.top > b.bottom || b.top > a.bottom)
		}

		@inline def intersect(a: Rectangle, b: Circle): Boolean = {
			@inline def clamp(a: Double, min: Double, max: Double) = if (a < min) min else if (a > max) max else a
			val cx = clamp(b.x, a.left, a.right)
			val cy = clamp(b.y, a.top, a.bottom)
			squaredDistance(b.x, b.y, cx, cy) <= b.squaredRadius
		}

		@inline def intersect(a: Circle, b: Circle): Boolean = {
			squaredDistance(a.x, a.y, b.y, b.y) <= (a.squaredRadius + b.squaredRadius)
		}

		@inline def intersect(a: Triangle, b: Triangle): Boolean = {
			List(a.AB, a.AC, a.BC).map(line => List(b.AB, b.AC, b.BC).filter(_ intersect line)).nonEmpty
		}

		@inline def intersect(t: Triangle, r: Rectangle): Boolean = {
			(Triangle(r.x, r.y, r.x + r.width, r.y, r.x, r.y + r.height) intersect t) ||
			(Triangle(r.x + r.width, r.y + r.height, r.x + r.width, r.y, r.x, r.y + r.height) intersect t)
		}

		@inline def intersect(t: Triangle, c: Circle): Boolean = {
			intersect(t.AB, c) || intersect(t.AC, c) || intersect(t.BC, c)
		}

		@inline def intersect(s: Segment, c: Circle): Boolean = {
			val dx = s.x2 - s.x1
			val dy = s.y2 - s.y1
			val dr = math.sqrt(dx * dx + dy * dy)
			val D = s.x1 * s.y2 - s.x2 * s.y1
			val discri = (c.radius * c.radius * dr * dr) - (D * D)
			discri > 0
		}

		@inline def squaredDistance(ax: Double, ay: Double, bx: Double, by: Double): Double = {
			val dx = ax - bx
			val dy = ay - by
			dx * dx + dy * dy
		}
	}
}
