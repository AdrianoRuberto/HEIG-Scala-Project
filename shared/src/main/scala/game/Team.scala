package game

import boopickle.Default._

/**
  * Created by Adriano on 17.05.2017.
  */
case class Team(player: Seq[Player])

object Team {
	implicit val pickler: Pickler[Team] = generatePickler[Team]
}