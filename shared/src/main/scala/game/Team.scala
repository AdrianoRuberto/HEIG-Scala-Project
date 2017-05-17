package game

import boopickle.DefaultBasic.{Pickler, PicklerGenerator}

/**
  * Created by Adriano on 17.05.2017.
  */
class Team(player: Seq[Player])

object Team {
	implicit val pickler: Pickler[Team] = PicklerGenerator.generatePickler[Team]
}