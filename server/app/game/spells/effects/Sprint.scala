package game.spells.effects

object Sprint extends SpellEffect {
	def cast(implicit ctx: SpellContext): Unit = {
		val ps = playerSkeleton
		if (!activated && ps.energy.current > 1 && ready) {
			playerSkeleton.speed.value *= 2
			playerSkeleton.energy.rate -= 45
			createTicker { _ =>
				println("tick", ps.energy.current)
				if (ps.energy.current < 1) cancel(ctx)
			}
			activate()
		} else {
			deactivate()
		}
	}

	def cancel(implicit ctx: SpellContext): Unit = {
		if (activated) {
			playerSkeleton.speed.value /= 2
			playerSkeleton.energy.rate += 45
			cancelTicker()
			deactivate()
			cooldown(1000)
		}
	}
}
