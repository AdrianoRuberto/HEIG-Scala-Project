package game.spells

import boopickle.Default._

sealed trait Spell

object Spell {
	case object Sprint extends Spell
	case object Sword extends Spell

	implicit val pickler: Pickler[Spell] = generatePickler[Spell]
}
