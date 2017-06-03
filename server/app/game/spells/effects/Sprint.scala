package game.spells.effects

object Sprint extends SpellEffect {
	def cast(implicit ctx: SpellContext): Unit = {
		val ps = ctx.player.skeleton
		if (!activated && ps.energy.current > 1 && ready) {
			ps.speed.value *= 2
			ps.energy.rate -= 45
			createTicker { _ =>
				if (ps.energy.current < 1) cancel(ctx)
			}
			activate()
		} else {
			deactivate()
		}
	}

	def cancel(implicit ctx: SpellContext): Unit = {
		if (activated) {
			ctx.player.skeleton.speed.value /= 2
			ctx.player.skeleton.energy.rate += 45
			cancelTicker()
			deactivate()
			cooldown(1000)
		}
	}
}
