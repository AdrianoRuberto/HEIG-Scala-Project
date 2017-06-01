package game.spells.icon
import engine.CanvasCtx

object BioticField extends SpellIcon {
	protected def drawIcon(ctx: CanvasCtx): Unit = {
		ctx.moveTo(20, 25)
		ctx.bezierCurveTo(0, 35, 10, 50, 30, 50)
		ctx.bezierCurveTo(50, 50, 60, 35, 40, 25)
		ctx.bezierCurveTo(55, 35, 45, 45, 30, 45)
		ctx.bezierCurveTo(15, 45, 5, 35, 20, 25)
		ctx.lineTo(20, 25)

		ctx.save()
		ctx.transform(0.6, 0, 0, 0.6, 12, 12)
		ctx.moveTo(20, 25)
		ctx.bezierCurveTo(0, 35, 10, 50, 30, 50)
		ctx.bezierCurveTo(50, 50, 60, 35, 40, 25)
		ctx.bezierCurveTo(55, 35, 45, 45, 30, 45)
		ctx.bezierCurveTo(15, 45, 5, 35, 20, 25)
		ctx.restore()

		ctx.save()
		ctx.transform(0.75, 0, -0.05, 0.75, 8.5, 2)
		ctx.moveTo(25, 10)
		ctx.lineTo(35, 10)
		ctx.lineTo(35, 20)
		ctx.lineTo(45, 20)
		ctx.lineTo(45, 30)
		ctx.lineTo(35, 30)
		ctx.lineTo(35, 40)
		ctx.lineTo(25, 40)
		ctx.lineTo(25, 30)
		ctx.lineTo(15, 30)
		ctx.lineTo(15, 20)
		ctx.lineTo(25, 20)
		ctx.lineTo(25, 10)
		ctx.restore()
	}
}
