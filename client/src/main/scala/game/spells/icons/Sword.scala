package game.spells.icons

import engine.CanvasCtx

object Sword extends SpellIcon {
	def drawIcon(ctx: CanvasCtx): Unit = {
		ctx.save()
		ctx.translate(10, 10)
		ctx.scale(2.0 / 3.0, 2.0 / 3.0)

		ctx.moveTo(5, 50)
		ctx.arc(5, 55, 5, -Math.PI / 2, 0, anticlockwise = true)
		ctx.lineTo(18, 48)
		ctx.lineTo(24, 54)
		ctx.lineTo(27, 50)
		ctx.lineTo(20, 40)
		ctx.lineTo(9, 32)
		ctx.lineTo(6, 35)
		ctx.lineTo(12, 41)
		ctx.lineTo(5, 50)

		ctx.moveTo(17, 35)
		ctx.lineTo(22, 38)
		ctx.lineTo(24, 42)
		ctx.lineTo(55, 14)
		ctx.lineTo(60, 0)
		ctx.lineTo(45, 4)
		ctx.lineTo(17, 35)

		ctx.restore()
	}
}
