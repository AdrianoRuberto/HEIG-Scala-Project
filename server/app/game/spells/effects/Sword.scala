package game.spells.effects

object Sword extends SpellEffect {
	def cast(implicit ctx: SpellContext): Unit = {
		activate()
		deactivate()
	}

	def cancel(implicit ctx: SpellContext): Unit = ()
}
