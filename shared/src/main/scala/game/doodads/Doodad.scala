package game.doodads

sealed abstract class Doodad ()

object Doodad {
	object Spell {
		case class Sword(x: Double, y: Double, angle: Double) extends Doodad
	}
}
