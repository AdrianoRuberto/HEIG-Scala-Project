package game.spells.effects

import game.spells.effects.SpellEffect.EffectInstance

object Flagellation extends SpellEffect {
	type Instance = FlagellationInstance
	def instantiate(ctx: SpellContext): FlagellationInstance = new FlagellationInstance(this, ctx)

	class FlagellationInstance(e: SpellEffect, c: SpellContext)
		extends EffectInstance(e: SpellEffect, c: SpellContext) {

		override def gain(): Unit = {
			player.skeleton.health.consume(100)
		}
	}
}
