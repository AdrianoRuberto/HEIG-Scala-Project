package game.doodads

import engine.geometry.Shape
import game.UID
import macros.pickle
import utils.Color

@pickle sealed trait Doodad

object Doodad {
	object Area {
		/** A static area display */
		@pickle case class StaticArea(shape: Shape, fill: Boolean = true, stroke: Boolean = true,
		                              fillColor: Color = Color(85, 170, 85, 0.1),
		                              strokeColor: Color = Color(85, 170, 85, 0.8), strokeWidth: Int = 2) extends Doodad

		/** A dynamic area display */
		@pickle case class DynamicArea(skeleton: UID) extends Doodad
	}

	object Debug {
		@pickle case class Point(skeleton: UID) extends Doodad
	}

	object Hud {
		@pickle case class KothStatus(skeleton: UID) extends Doodad
		@pickle case class DeathScreen(duration: Double) extends Doodad
		@pickle case class VictoryScreen(msg: String, color: String, skeleton: UID) extends Doodad
	}

	object Spell {
		@pickle case class Sword(x: Double, y: Double, angle: Double) extends Doodad
	}
}
