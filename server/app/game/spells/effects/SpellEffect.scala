package game.spells.effects

import game.UID
import game.protocol.enums.Spell
import game.server.Ticker

abstract class SpellEffect {
	private var tickers: Map[UID, Ticker] = Map.empty

	def cast(implicit ctx: SpellContext): Unit
	def cancel(implicit ctx: SpellContext): Unit

	@inline protected def activated(implicit ctx: SpellContext): Boolean = ctx.skeleton.activated.value
	@inline protected def ready(implicit ctx: SpellContext): Boolean = ctx.skeleton.cooldown.ready

	@inline protected def activate()(implicit ctx: SpellContext): Unit = ctx.skeleton.activated.value = true
	@inline protected def deactivate()(implicit ctx: SpellContext): Unit = ctx.skeleton.activated.value = false
	@inline protected def cooldown(duration: Double)(implicit ctx: SpellContext): Unit = ctx.skeleton.cooldown.start(duration)

	protected def createTicker(tickImpl: Double => Unit)(implicit ctx: SpellContext): Unit = {
		cancelTicker()
		val ticker = new Ticker {
			def tick(dt: Double): Unit = tickImpl(dt)
			def unregister(): Unit = {
				tickers -= ctx.initiator
				ctx.game.unregisterTicker(this)
			}
		}
		tickers += (ctx.initiator -> ticker)
		ctx.game.registerTicker(ticker)
	}

	protected def cancelTicker()(implicit ctx: SpellContext): Unit = {
		for (ticker <- tickers.get(ctx.initiator)) ticker.unregister()
	}
}

object SpellEffect {
	def forSpell(spell: Spell): SpellEffect = spell match {
		case Spell.Sprint => Sprint
		case Spell.Sword => ???
	}
}
