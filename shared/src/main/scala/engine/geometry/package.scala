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

		@inline def squaredDistance(ax: Double, ay: Double, bx: Double, by: Double): Double = {
			val dx = ax - bx
			val dy = ay - by
			dx * dx + dy * dy
		}
	}
}
