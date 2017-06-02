package game.spells.effects

import game.UID
import game.protocol.enums.Spell
import game.server.{BasicGame, Ticker}
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}

abstract class SpellEffect {
	private var tickers: Map[UID, Ticker] = Map.empty

	def cast(implicit ctx: SpellContext): Unit
	def cancel(implicit ctx: SpellContext): Unit

	@inline protected def game(implicit ctx: SpellContext): BasicGame = ctx.game
	@inline protected def skeleton(implicit ctx: SpellContext): SpellSkeleton = ctx.skeleton
	@inline protected def playerSkeleton(implicit ctx: SpellContext): CharacterSkeleton = ctx.playerSkeleton
	@inline protected def player(implicit ctx: SpellContext): BasicGame#UIDOps = ctx.player
	@inline protected def initiator(implicit ctx: SpellContext): UID = ctx.initiator

	@inline protected def activated(implicit ctx: SpellContext): Boolean = skeleton.activated.value
	@inline protected def ready(implicit ctx: SpellContext): Boolean = skeleton.cooldown.ready

	@inline protected def activate()(implicit ctx: SpellContext): Unit = skeleton.activated.value = true
	@inline protected def deactivate()(implicit ctx: SpellContext): Unit = skeleton.activated.value = false
	@inline protected def cooldown(duration: Double)
	                              (implicit ctx: SpellContext): Unit = skeleton.cooldown.start(duration)

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
