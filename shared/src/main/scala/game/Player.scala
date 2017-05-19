package game

import boopickle.Default._

case class Player(name: String, bot: Boolean = false, uid: Long = -1)

object Player {
	implicit val pickler: Pickler[Player] = generatePickler[Player]
}
