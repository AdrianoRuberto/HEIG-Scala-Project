package game.spells.effects

import engine.geometry
import game.UID
import game.server.{BasicGame, Ticker}
import game.skeleton.concrete.SpellSkeleton
import game.spells.Spell
import game.spells.effects.SpellEffect._
import scala.language.implicitConversions

abstract class SpellEffect {
	type Instance <: EffectInstance
	private var instances: Map[UID, Instance] = Map.empty
	def instantiate(ctx: SpellContext): Instance

	def available(ctx: SpellContext): Boolean = {
		// Cooldown is ready and player has enough energy
		ctx.skeleton.cooldown.ready && ctx.skeleton.spell.value.cost.forall(_ <= ctx.player.skeleton.energy.current)
	}

	final def cast(ctx: SpellContext): Unit = {
		if (available(ctx)) {
			if (!ctx.skeleton.activated.value) {
				ctx.skeleton.activated.value = true
				val instance = createInstance(ctx)
				for (cost <- ctx.skeleton.spell.value.cost) {
					ctx.player.skeleton.energy.consume(cost)
				}
				instance.gain()
			}
		} else {
			ctx.skeleton.activated.set(false, force = true)
		}
	}

	final def cancel(ctx: SpellContext): Unit = {
		for (instance <- instances.get(ctx.initiator)) instance.cancel()
	}

	private def createInstance(ctx: SpellContext): Instance = {
		collectInstance(ctx)
		instances.get(ctx.initiator) match {
			case Some(instance) => instance
			case None =>
				val instance = instantiate(ctx)
				instances += (ctx.initiator -> instance)
				ctx.game.registerTicker(instance.ticker)
				instance
		}
	}

	private def collectInstance(ctx: SpellContext): Unit = {
		for (instance <- instances.get(ctx.initiator)) {
			instance.ticker.remove()
			instances -= ctx.initiator
		}
	}
}

object SpellEffect {
	def forSpell(spell: Spell): SpellEffect = spell match {
		case Spell.Sprint => Sprint
		case Spell.Sword => Sword
	}

	class EffectInstance(effect: SpellEffect, val ctx: SpellContext) { self =>
		private var time = 0.0

		protected var duration: Double = 0.0
		protected var cooldown: Double = 0.0

		val game: BasicGame = ctx.game
		val player: ctx.game.UIDOps = ctx.player
		val skeleton: SpellSkeleton = ctx.skeleton
		val point: geometry.Vector2D = ctx.point
		val initiator: UID = ctx.initiator

		private[SpellEffect] val ticker = new Ticker {
			def tick(dt: Double): Unit = {
				time += dt
				if (duration > 0.0 && time >= duration) cancel()
				else self.tick(dt)
			}

			def remove(): Unit = {
				ctx.game.unregisterTicker(this)
			}
		}

		def gain(): Unit = ()
		def tick(dt: Double): Unit = ()
		def lose(): Unit = ()

		def cancel(): Unit = if (duration <= 0 || time >= duration) remove()
		final def remove(): Unit = {
			lose()
			if (cooldown > 0.0) skeleton.cooldown.start(cooldown)
			skeleton.activated.value = false
			effect.collectInstance(ctx)
		}

		@inline protected implicit final def uidToOps(uid: UID): ctx.game.UIDOps = new ctx.game.UIDOps(uid)
	}
}
