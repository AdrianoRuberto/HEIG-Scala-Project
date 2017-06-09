package game.spells.effects

import engine.geometry
import game.UID
import game.server.{BasicGame, Ticker}
import game.skeleton.concrete.SpellSkeleton
import scala.language.implicitConversions

class SpellEffectInstance(effect: SpellEffect, val ctx: SpellContext) { self =>
	private val timestamp = ctx.game.time

	val duration: Double = 0.0
	val cooldown: Double = 0.0

	val game: BasicGame = ctx.game
	val player: ctx.game.UIDOps = ctx.player
	val skeleton: SpellSkeleton = ctx.skeleton
	val point: geometry.Vector2D = ctx.point
	val initiator: UID = ctx.initiator

	private[effects] val ticker = new Ticker {
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

	@inline protected implicit final def uidToOps(uid: UID): ctx.game.UIDOps = new ctx.game.UIDOps(uid)
}
