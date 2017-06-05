package game.doodads

import game.UID

sealed abstract class Doodad ()

object Doodad {
	object Debug {
		case class Point(skeleton: UID) extends Doodad
	}

	object Spell {
		case class Sword(x: Double, y: Double, angle: Double) extends Doodad
	}
}
