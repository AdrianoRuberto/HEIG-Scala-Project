package game.doodads

import engine.geometry.Shape
import game.UID

sealed trait Doodad

object Doodad {
	object Area {
		case class StaticArea(shape: Shape,
		                      fill: Boolean = true,
		                      fillColor: String = "rgba(85, 170, 85, 0.1)",
		                      stroke: Boolean = true,
		                      strokeColor: String = "rgba(85, 170, 85, 0.8)",
		                      strokeWidth: Int = 2) extends Doodad
		case class DynamicArea(skeleton: UID) extends Doodad
	}

	object Debug {
		case class Point(skeleton: UID) extends Doodad
	}

	object Interface {
		case class Koth(skeleton: UID) extends Doodad
		case class DeathScreen(duration: Double) extends Doodad
		case class VictoryScreen(msg: String, color: String, skeleton: UID) extends Doodad
	}

	object Spell {
		case class Sword(x: Double, y: Double, angle: Double) extends Doodad
	}
}
