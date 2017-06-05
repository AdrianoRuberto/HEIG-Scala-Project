package game.spells.effects

object Sprint extends SpellEffect {
	override def available(ctx: SpellContext): Boolean = {
		ctx.skeleton.cooldown.ready && ctx.player.skeleton.energy.current > 1
	}

	def instantiate(ctx: SpellContext) = new SpellEffectInstance(this, ctx) {
		override val cooldown: Double = 1000

		player.skeleton.speed.value *= 2
		player.skeleton.energy.rate -= 45

		override def tick(dt: Double): Unit = {
			if (player.skeleton.energy.current < 1) cancel()
		}

		override def end(): Unit = {
			player.skeleton.speed.value /= 2
			player.skeleton.energy.rate += 45
		}
	}
}
