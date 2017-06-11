package utils

import engine.geometry._
import java.lang.Math._

object CollisionDetection {
	def collide(x: Double, y: Double, dx: Double, dy: Double, walls: Iterable[Shape]): Vector2D = {
		val path = Segment(x, y, dx, dy)
		val candidates = walls.filter(w => !(w contains path.A)).filter(w => w.boundingBox intersect path.boundingBox)
		val intersections = candidates.flatMap(intersect(path))

		if (intersections.isEmpty) Vector2D(dx, dy)
		else intersections.minBy(_ <-> path.A) - path.AB.normalized
	}

	private def intersect(path: Segment)(wall: Shape): List[Vector2D] = wall match {
		case c: Circle => // TODO: fixme
			val centerRej = -((c.center - path.A) reject path.AB)
			val squaredRadius = c.radius * c.radius
			val squaredNorm = centerRej dot centerRej

			if (squaredNorm > squaredRadius) Nil
			else {
				val u = path.AB.normalized * sqrt(squaredRadius - squaredNorm)
				List(c.center + centerRej + u, c.center + centerRej - u)
			}

		case s: Segment =>
			val t = ((s.A - path.A) cross s.AB) / (path.AB cross s.AB)
			val u = ((path.A - s.A) cross path.AB) / (s.AB cross path.AB)
			if (0.0 <= t && t <= 1.0 && 0.0 <= u && u <= 1.0) List(path.A + t * path.AB)
			else Nil

		case Rectangle(x, y, width, height) =>
			val sides = List(Segment(x, y, x + width, y),
				Segment(x + width, y, x + width, y + height),
				Segment(x, y + height, x + width, y + height),
				Segment(x, y, x, y + height))
			sides.flatMap(intersect(path))

		case t: Triangle =>
			val sides = List(Segment(t.A, t.B), Segment(t.A, t.C), Segment(t.B, t.C))
			sides.flatMap(intersect(path))

		case _ => Nil
	}
}
