package engine.geometry

case class Rectangle (x: Double, y: Double, width: Double, height: Double) extends Shape {
	@inline def size: Size = Size(width, height)

	@inline def left: Double = x
	@inline def right: Double = x + width
	@inline def top: Double = y
	@inline def bottom: Double = y + height

	@inline def boundingBox: Rectangle = this

	@inline def contains(x: Double, y: Double): Boolean = !(x < left || x > right || y < top || y > bottom)

	@inline def contains(r: Rectangle): Boolean = !(left > r.left || right < r.right || top > r.top || bottom < r.bottom)
	@inline def contains(c: Circle): Boolean = contains(c.boundingBox)

	@inline def intersect(r: Rectangle): Boolean = g.intersect(this, r)
	@inline def intersect(c: Circle): Boolean = g.intersect(this, c)
}

object Rectangle {
	@inline def apply(corner: Point, size: Size): Rectangle = apply(corner.x, corner.y, size.width, size.height)
}
