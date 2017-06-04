package engine.utils

import engine.geometry.{Rectangle, Segment, Shape, Triangle, Vector}

object CollisionDetection {

	def collide(x: Double, y: Double, dx: Double, dy: Double, walls: Iterable[Shape]): Vector = {
		val path = Segment(x, y, dx, dy)
		val intersected = walls.filter(path.intersect)
		println(intersected)

		if (intersected.nonEmpty) intersected.flatMap(collide(path, _)).minBy(vec => vec.squaredDistance(x, y))
		else Vector(dx, dy)
	}

	private def collide(path: Segment, wall: Shape): List[Vector] = wall match {
		// case Circle(x, y, radius) =>

		case s: Segment =>
			val det = path.A * s.B - s.A * path.B
			if (det == 0) Nil
			else {
				val x = (s.B * path.C - path.B * s.C) / det
				val y = (path.A * s.C - s.A * path.C) / det
				List(Vector(x, y))
			}
		case Rectangle(x, y, width, height) =>
			val sides = List(Segment(x, y, x + width, y),
				Segment(x + width, y, x + width, y + height),
				Segment(x, y + height, x + width, y + height),
				Segment(x, y, x, y + height))
			sides.flatMap(collide(path, _))
		case t: Triangle =>
			val sides = List(t.AB, t.AC, t.BC)
			sides.flatMap(collide(path, _))
		case _ => List(Vector(path.x2, path.y2))
	}

}
