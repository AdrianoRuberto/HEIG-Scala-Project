package game.maps

import boopickle.Default._
import engine.geometry.{Point, Shape}

sealed abstract class GameMap(val geometry: Seq[Shape],
                              val spawns: List[Point] = Nil) {

}

object GameMap {
	case object Illios extends GameMap(
		geometry = KingOfTheHill.illios,
		spawns = List(Point(100, 300), Point(400, 300))
	)

	implicit val pickler: Pickler[GameMap] = generatePickler[GameMap]
}
