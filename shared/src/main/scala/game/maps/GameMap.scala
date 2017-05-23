package game.maps

import boopickle.Default._
import engine.geometry.Shape

sealed abstract class GameMap(val shapes: Seq[Shape])

object GameMap {
	case object Illios extends GameMap(KingOfTheHill.illios)

	implicit val pickler: Pickler[GameMap] = generatePickler[GameMap]
}
