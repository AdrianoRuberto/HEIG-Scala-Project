package game

import boopickle.DefaultBasic._

case class Team(name: String, player: Seq[Player])

object Team {
	implicit val pickler: Pickler[Team] = PicklerGenerator.generatePickler[Team]
}
