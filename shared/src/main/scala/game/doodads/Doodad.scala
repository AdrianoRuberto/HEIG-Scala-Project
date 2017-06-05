package game.doodads

import game.UID

sealed trait Doodad

object Doodad {
	object Area {
		case class DynamicArea(skeleton: UID) extends Doodad
	}

	object Debug {
		case class Point(skeleton: UID) extends Doodad
	}

	object Interface {
		case class Koth(skeleton: UID) extends Doodad
		case class DeathScreen(duration: Double) extends Doodad
	}

	object Spell {
		case class Sword(x: Double, y: Double, angle: Double) extends Doodad
	}
}
