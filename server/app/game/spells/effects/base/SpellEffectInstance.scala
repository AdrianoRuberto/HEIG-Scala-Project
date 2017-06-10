package game.spells.effects.base

import engine.geometry.Vector2D
import game.UID
import game.server.{BasicGame, Ticker}
import game.skeleton.core.SpellSkeleton
import scala.language.implicitConversions

class SpellEffectInstance(effect: SpellEffect, val ctx: SpellContext) { self =>
	implicit val implicitCtx: SpellContext = ctx
	private val timestamp = ctx.game.time

	val duration: Double = 0.0
	val cooldown: Double = 0.0

	val game: BasicGame = ctx.game
	val player: ctx.game.UIDOps = ctx.player
	val skeleton: SpellSkeleton = ctx.skeleton
	val point: Vector2D = ctx.point
	val initiator: UID = ctx.initiator

	private[base] val ticker = new Ticker {
		def tick(dt: Double): Unit = {
			if (duration > 0.0 && (game.time - timestamp) >= duration) cancel()
			else self.tick(dt)
		}

		def remove(): Unit = game.tickers -= this
	}

	def tick(dt: Double): Unit = ()
	def end(): Unit = ()

	def cancel(): Unit = {
		if (duration <= 0 || (game.time - timestamp) >= duration) remove()
	}

	final def remove(): Unit = {
		end()
		if (cooldown > 0.0) skeleton.cooldown.start(cooldown)
		skeleton.activated.value = false
		ticker.remove()
		effect.instances.synchronized {
			effect.instances -= ctx.initiator
		}
	}
}
