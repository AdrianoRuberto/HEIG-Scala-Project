package game.spells.effects

import game.server.modes.ctf.CaptureTheFlagGame
import game.spells.effects.base.{SimpleEffect, SpellContext}

object DropTheFlag extends SimpleEffect {
	override def apply(ctx: SpellContext): Unit = {
		ctx.game.asInstanceOf[CaptureTheFlagGame].dropFlag(ctx.initiator)
	}
}
