package game.shared

import boopickle.DefaultBasic._

case class PlayerInfo(uid: UID, name: String, bot: Boolean)

object PlayerInfo {
	implicit val pickler: Pickler[PlayerInfo] = PicklerGenerator.generatePickler[PlayerInfo]
}
