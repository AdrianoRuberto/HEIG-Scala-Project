package game.spells.effects

import game.spells.effects.SpellEffect.EffectInstance

object Sprint extends SpellEffect {
	type Instance = SprintInstance
	def instantiate(ctx: SpellContext): SprintInstance = new SprintInstance(this, ctx)

	override def available(ctx: SpellContext): Boolean = {
		ctx.skeleton.cooldown.ready && ctx.player.skeleton.energy.current > 1
	}

	class SprintInstance(e: SpellEffect, c: SpellContext)
		extends EffectInstance(e: SpellEffect, c: SpellContext) {

		cooldown = 1000

		override def gain(): Unit = {
			player.skeleton.speed.value *= 2
			player.skeleton.energy.rate -= 45
		}

		override def tick(dt: Double): Unit = {
			if (player.skeleton.energy.current < 1) cancel()
		}

		override def lose(): Unit = {
			player.skeleton.speed.value /= 2
			player.skeleton.energy.rate += 45
		}
	}
}
