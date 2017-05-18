package game

import boopickle.Default._

case class Team(player: Seq[Player])

object Team {
	implicit val pickler: Pickler[Team] = generatePickler[Team]
}
