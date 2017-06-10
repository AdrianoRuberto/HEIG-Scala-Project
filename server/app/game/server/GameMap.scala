package game.server

import engine.geometry._
import utils.Color

sealed abstract class GameMap(val geometry: Seq[Shape],
                              val color: Color = Color("#333"),
                              val spawns: List[Vector2D] = Nil)

object GameMap {
	object Illios extends GameMap(
		spawns = List(Vector2D(-880, -400), Vector2D(880, -400)),
		color = Color("#333"),
		geometry = Seq(
			// Left side
			Segment(-22, -13, -22, 7),
			Segment(-22, 7, -7, 7),
			Rectangle(-7, 5, 2, 2),
			Rectangle(-18, -3, 6, 7),
			Rectangle(-10, -3, 5, 4),
			Rectangle(-13, -10, 7, 4),
			Triangle(-7, 7, -10, 7, -7, 5),
			// Center
			Segment(-22, -13, 22, -13),
			Segment(-4, -11, 4, -11),
			Segment(-4, -11, -4, -9),
			Segment(-4, -7, -4, -5),
			Segment(4, -11, 4, -9),
			Segment(4, -7, 4, -5),
			Segment(-4, -5, -1, -5),
			Segment(4, -5, 1, -5),
			Triangle(0, -12, -1, -11, 1, -11),
			Segment(-5, 7, -5, 15),
			Segment(-5, 15, 5, 15),
			Segment(5, 15, 5, 7),
			Triangle(-3, 7, -3, 9, -1, 7),
			Triangle(3, 7, 3, 9, 1, 7),
			Triangle(-3, 13, -3, 11, -1, 13),
			Triangle(3, 13, 3, 11, 1, 13),
			// Right side
			Segment(22, -13, 22, 7),
			Segment(22, 7, 7, 7),
			Rectangle(5, 5, 2, 2),
			Rectangle(12, -3, 6, 7),
			Rectangle(5, -3, 5, 4),
			Rectangle(6, -10, 7, 4),
			Triangle(7, 7, 10, 7, 7, 5)
		).map(_ scale 50)
	)

	object Nepal extends GameMap(
		spawns = List(Vector2D(-1680, 0), Vector2D(1680, 0)),
		geometry = Seq(
			// Border
			Segment(-27, -7, -18, -7),
			Segment(-18, -7, -18, -11),
			Segment(-18, -11, 18, -11),
			Segment(18, -11, 18, -7),
			Segment(18, -7, 27, -7),
			Segment(27, -7, 27, 8),
			Segment(27, 8, 4, 8),
			Segment(2, 10, 2, 12),
			Segment(2, 12, -2, 12),
			Segment(-2, 12, -2, 10),
			Segment(-4, 8, -27, 8),
			Segment(-27, 8, -27, -7),

			// Left
			Rectangle(-18, -11, 6, 2),
			Rectangle(-16, -7, 4, 5),
			Rectangle(-12, -3, 2, 1),
			Rectangle(-16, 2, 4, 4),
			Rectangle(-10, 3, 2, 2),

			// Right
			Rectangle(12, -11, 6, 2),
			Rectangle(12, -7, 4, 5),
			Rectangle(10, -3, 2, 1),
			Rectangle(12, 2, 4, 4),
			Rectangle(8, 3, 2, 2),

			// Top
			Rectangle(-4, -11, 1, 2),
			Rectangle(3, -11, 1, 2),
			Rectangle(-8, -3, 5, 1),
			Rectangle(3, -3, 5, 1),
			Rectangle(-4, -7, 1, 4),
			Rectangle(3, -7, 1, 4),
			Rectangle(-3, -2, 1, 1),
			Rectangle(2, -2, 1, 1),
			Triangle(-3, -3, -3, -2, -2, -2),
			Triangle(2, -2, 3, -2, 3, -3),

			// Bottom
			Rectangle(-3, 1, 1, 2),
			Rectangle(2, 1, 1, 2),
			Rectangle(-2, 2, 1, 1),
			Rectangle(1, 2, 1, 1),
			Rectangle(-3, 5, 6, 1),
			Rectangle(-1, 6, 2, 2),
			Triangle(-3, 6, -1, 8, -1, 6),
			Triangle(1, 6, 1, 8, 3, 6),
			Triangle(-4, 8, -4, 10, -2, 10),
			Triangle(2, 10, 4, 10, 4, 8)
		) map (_ scale 80)
	)
}
