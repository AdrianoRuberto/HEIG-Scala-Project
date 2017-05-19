package game

import boopickle.DefaultBasic._

case class TeamInfo(uid: UID, name: String, players: Seq[PlayerInfo])

object TeamInfo {
	implicit val pickler: Pickler[TeamInfo] = PicklerGenerator.generatePickler[TeamInfo]
}
