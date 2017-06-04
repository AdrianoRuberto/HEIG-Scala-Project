package engine.geometry

trait ConvexPolygon extends Shape {
	def vertices: Seq[Vector2D]

	def axes: Set[Vector2D] = (vertices :+ vertices.head).sliding(2).map { case Seq(x, y) => (y - x).normalized }.toSet
}
