package engine.geometry

import boopickle.DefaultBasic._

case class Point(x: Double, y: Double) {
	def distance(p: Point): Double = {
		val dx = x - p.x
		val dy = y - p.y
		Math.sqrt(dx * dx + dy * dy)
	}
}

object Point {
	implicit val pickler: Pickler[Point] = PicklerGenerator.generatePickler[Point]
}
