package game.spells.icons

import engine.CanvasCtx

object Sprint extends SpellIcon {
	def drawIcon(ctx: CanvasCtx): Unit = {
		ctx.moveTo(10, 20)
		ctx.lineTo(35, 20)
		ctx.lineTo(35, 10)
		ctx.lineTo(50, 30)
		ctx.lineTo(35, 50)
		ctx.lineTo(35, 40)
		ctx.lineTo(10, 40)
		ctx.lineTo(10, 20)

		ctx.moveTo(10, 17)
		ctx.lineTo(27, 16)
		ctx.lineTo(25, 13)
		ctx.lineTo(10, 17)

		ctx.moveTo(10, 43)
		ctx.lineTo(28, 43)
		ctx.lineTo(26, 46)
		ctx.lineTo(10, 43)

		ctx.moveTo(10, 45)
		ctx.lineTo(20, 47)
		ctx.lineTo(18, 49)
		ctx.lineTo(10, 45)
	}
}
