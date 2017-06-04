package game.maps

import boopickle.Default._
import engine.geometry.{ColoredShape, Vector2D}

sealed abstract class GameMap(val geometry: Seq[ColoredShape],
                              val spawns: List[Vector2D] = Nil) {

}

object GameMap {
	case object Illios extends GameMap(
		geometry = KingOfTheHill.illios,
		spawns = List(Vector2D(100, 300), Vector2D(400, 300))
	)

	implicit val pickler: Pickler[GameMap] = generatePickler[GameMap]
}
