package game.doodads

sealed abstract class Doodad ()

object Doodad {
	object Spell {
		case object Sword extends Doodad
	}
}
