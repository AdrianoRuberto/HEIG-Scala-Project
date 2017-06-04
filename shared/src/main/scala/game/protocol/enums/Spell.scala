package game.protocol.enums

sealed abstract class Spell (val cost: Option[Double] = None)

object Spell {
	case object Sprint extends Spell
	case object Sword extends Spell(Some(30))
}
