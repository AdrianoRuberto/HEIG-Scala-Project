package game

import boopickle.DefaultBasic._

case class Player(name: String, uid: UID, bot: Boolean)

object Player {
	implicit val pickler: Pickler[Player] = PicklerGenerator.generatePickler[Player]
}
