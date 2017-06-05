package game.spells

sealed abstract class Spell (val cost: Option[Double] = None)

object Spell {
	case object Sprint extends Spell
	case object Sword extends Spell(cost = Some(30))
	case object Flagellation extends Spell
}
