package game

import boopickle.Default._

case class Player(name: String)

object Player {
	implicit val pickler: Pickler[Player] = generatePickler[Player]
}
