package game.maps

import boopickle.Default._
import engine.geometry.{ColoredShape, Vector}

sealed abstract class GameMap(val geometry: Seq[ColoredShape],
                              val spawns: List[Vector] = Nil) {

}

object GameMap {
	case object Illios extends GameMap(
		geometry = KingOfTheHill.illios,
		spawns = List(Vector(100, 300), Vector(400, 300))
	)

	implicit val pickler: Pickler[GameMap] = generatePickler[GameMap]
}
