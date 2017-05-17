package game

import boopickle.Default._

/**
  * Created by Adriano on 17.05.2017.
  */
case class Player(name: String)

object Player {
	implicit val pickler: Pickler[Player] = generatePickler[Player]
}
