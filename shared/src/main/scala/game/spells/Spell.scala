package game.spells

import macros.pickle

@pickle sealed abstract class Spell (val cost: Option[Double] = None)

object Spell {
	@pickle case object DropTheFlag extends Spell
	@pickle case object Sprint extends Spell
	@pickle case object Sword extends Spell(cost = Some(30))
	@pickle case object Flagellation extends Spell
	@pickle case object BioticField extends Spell(cost = Some(50))
}
