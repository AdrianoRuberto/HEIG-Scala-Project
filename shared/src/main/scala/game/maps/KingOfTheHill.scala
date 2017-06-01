package game.maps

import engine.geometry._
import scala.language.implicitConversions

private[maps] object KingOfTheHill {

	val illios = Seq(
		// Left side
		Segment(-22, -13, -22, 25),
		Segment(-22, 7, -7, 7),
		Rectangle(-7, 5, 2, 2),
		Rectangle(-18, -3, 6, 7),
		Rectangle(-10, -3, 5, 4),
		Rectangle(-13, -10, 7, 4),
		Triangle(-7, 7, -10, 7, -7, 5),
		// Center
		Circle(0, 0, 3),
		Segment(-22, -13, 22, -13),
		Segment(-4, -11, 4, -11),
		Segment(-4, -11, -4, -9),
		Segment(-4, -7, -4, -5),
		Segment(4, -11, 4, -9),
		Segment(4, -7, 4, -5),
		Segment(-4, -5, -1, -5),
		Segment(4, -5, 1, -5),
		Triangle(0, -12, -1, -11, 1, -11),
		// Right side

		Segment(22, -13, 22, 7)
	).map(_ scale 20 colored "#333")
}
