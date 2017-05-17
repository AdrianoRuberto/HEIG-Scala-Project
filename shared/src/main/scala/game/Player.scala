package game

import boopickle.DefaultBasic.{Pickler, PicklerGenerator}

/**
  * Created by Adriano on 17.05.2017.
  */
class Player(name: String)

object Player {
	implicit val pickler: Pickler[Player] = PicklerGenerator.generatePickler[Player]
}
