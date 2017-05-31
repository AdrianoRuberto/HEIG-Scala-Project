package game.maps

import engine.geometry._
import scala.language.implicitConversions

private[maps] object KingOfTheHill {

	private val size: Double = 20

	val illios = Seq(
		// Left side
		ColoredShape(Segment(-22, -13, -22, 25) sized size),
		ColoredShape(Segment(-22, 7, -7, 7) sized size),
		ColoredShape(Rectangle(-7, 5, 2, 2) sized size),
		ColoredShape(Rectangle(-18, -3, 6, 7) sized size),
		ColoredShape(Rectangle(-10, -3, 5, 4) sized size),
		ColoredShape(Rectangle(-13, -10, 7, 4) sized size),
		ColoredShape(Triangle(-7, 7, -10, 7, -7, 5) sized size),
		// Center
		ColoredShape(Circle(0, 0, 3) sized size),
		ColoredShape(Segment(-22, -13, 22, -13) sized size),
		ColoredShape(Segment(-4, -11, 4, -11) sized size),
		ColoredShape(Segment(-4, -11, -4, -9) sized size),
		ColoredShape(Segment(-4, -7, -4, -5) sized size),
		ColoredShape(Segment(4, -11, 4, -9) sized size),
		ColoredShape(Segment(4, -7, 4, -5) sized size),
		ColoredShape(Segment(-4, -5, -1, -5) sized size),
		ColoredShape(Segment(4, -5, 1, -5) sized size),
		ColoredShape(Triangle(0, -12, -1, -11, 1, -11) sized size),
		// Right side

		ColoredShape(Segment(22, -13, 22, 7) sized size)
	)
}
