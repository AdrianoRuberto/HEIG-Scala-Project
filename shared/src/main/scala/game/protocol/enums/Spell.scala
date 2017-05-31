package game.protocol.enums

sealed trait Spell

object Spell {
	case object Sprint extends Spell
	case object Sword extends Spell
}
