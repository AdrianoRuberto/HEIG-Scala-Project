package game.spells.effects.base

import game.UID
import game.spells.Spell
import game.spells.effects.{BioticField, Flagellation, Sprint, Sword}

abstract class SpellEffect {
	private[base] var instances: Map[UID, SpellEffectInstance] = Map.empty
	def instantiate(ctx: SpellContext): SpellEffectInstance

	def available(ctx: SpellContext): Boolean = {
		// Cooldown is ready and player has enough energy
		ctx.skeleton.cooldown.ready && ctx.skeleton.spell.value.cost.forall(_ <= ctx.player.skeleton.energy.current)
	}

	final def cast(ctx: SpellContext): Unit = {
		if (available(ctx)) {
			if (!ctx.skeleton.activated.value) {
				// Remove previous instance
				for (instance <- instances.get(ctx.initiator)) {
					instance.remove()
				}

				// Activate spell
				ctx.skeleton.activated.value = true

				// Remove spell cost from player energy
				for (cost <- ctx.skeleton.spell.value.cost) {
					ctx.player.skeleton.energy.consume(cost)
				}

				// Create new instance
				val instance = instantiate(ctx)
				instances.synchronized {
					instances += (ctx.initiator -> instance)
				}
				ctx.game.tickers += instance.ticker
			}
		} else {
			ctx.skeleton.activated.set(false, force = true)
		}
	}

	final def cancel(ctx: SpellContext): Unit = {
		for (instance <- instances.get(ctx.initiator)) instance.cancel()
	}
}

object SpellEffect {
	def forSpell(spell: Spell): SpellEffect = spell match {
		case Spell.Sprint => Sprint
		case Spell.Sword => Sword
		case Spell.BioticField => BioticField
		case Spell.Flagellation => Flagellation
	}
}
