package game.spells.effects

import game.spells.effects.base.{SimpleEffect, SpellContext, SpellEffect}

object Flagellation extends SpellEffect with SimpleEffect {
	override def apply(ctx: SpellContext): Unit = {
		ctx.player.skeleton.health.consume(100)
	}
}
