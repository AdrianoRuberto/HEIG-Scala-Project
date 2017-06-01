package game.maps

import engine.geometry._
import scala.language.implicitConversions

private[maps] object KingOfTheHill {

	private val size: Double = 20
	private implicit val wallColor = ColoredShape.DefaultColor("#333")

	val illios = Seq[ColoredShape](
		// Left side
		Segment(-22, -13, -22, 25) scale size,
		Segment(-22, 7, -7, 7) scale size,
		Rectangle(-7, 5, 2, 2) scale size,
		Rectangle(-18, -3, 6, 7) scale size,
		Rectangle(-10, -3, 5, 4) scale size,
		Rectangle(-13, -10, 7, 4) scale size,
		Triangle(-7, 7, -10, 7, -7, 5) scale size,
		// Center
		Circle(0, 0, 3) scale size,
		Segment(-22, -13, 22, -13) scale size,
		Segment(-4, -11, 4, -11) scale size,
		Segment(-4, -11, -4, -9) scale size,
		Segment(-4, -7, -4, -5) scale size,
		Segment(4, -11, 4, -9) scale size,
		Segment(4, -7, 4, -5) scale size,
		Segment(-4, -5, -1, -5) scale size,
		Segment(4, -5, 1, -5) scale size,
		Triangle(0, -12, -1, -11, 1, -11) scale size,
		// Right side

		Segment(22, -13, 22, 7) scale size
	)
}
