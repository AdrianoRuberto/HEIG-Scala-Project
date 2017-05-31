package game.maps

import engine.geometry.{Circle, ColoredShape, Rectangle, Triangle}

private[maps] object KingOfTheHill {
	val illios = Seq(
		ColoredShape(Rectangle(200, 200, 10, 200)),
		ColoredShape(Circle(10, 10, 5)),
		ColoredShape(Triangle(50, 50, 100, 75, 100, 25))
	)
}
