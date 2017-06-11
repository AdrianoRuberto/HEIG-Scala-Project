package game.spells.icons
import engine.CanvasCtx
import utils.Color

object DropTheFlag extends SpellIcon {
	protected def drawIcon(ctx: CanvasCtx): Unit = {
		ctx.save()
		ctx.translate(0, -3)
		ctx.beginPath()
		drawFlag(ctx)
		ctx.restore()

		ctx.save()
		ctx.translate(20, 7)
		ctx.scale(0.8, 0.8)
		ctx.moveTo(15, 30)
		ctx.lineTo(15, 42.5)
		ctx.lineTo(10, 42.5)
		ctx.lineTo(17.5, 50)
		ctx.lineTo(25, 42.5)
		ctx.lineTo(20, 42.5)
		ctx.lineTo(20, 30)
		ctx.lineTo(15, 30)
		ctx.restore()
	}

	def drawFlag(ctx: CanvasCtx, fillColor: Option[Color] = None): Unit = {
		for (color <- fillColor) {
			ctx.beginPath()
			ctx.lineTo(18, 15)
			ctx.lineTo(20, 15)
			ctx.bezierCurveTo(30, 10, 30, 20, 40, 15)
			ctx.lineTo(40, 30)
			ctx.bezierCurveTo(30, 35, 30, 25, 20, 30)
			ctx.lineTo(18, 30)
			ctx.lineTo(18, 15)
			ctx.save()
			ctx.fillStyle = color.toString
			ctx.fill()
			ctx.restore()
			ctx.beginPath()
		}
		ctx.moveTo(18, 45)
		ctx.lineTo(18, 15)
		ctx.lineTo(20, 15)
		ctx.bezierCurveTo(30, 10, 30, 20, 40, 15)
		ctx.lineTo(40, 30)
		ctx.bezierCurveTo(30, 35, 30, 25, 20, 30)
		ctx.lineTo(20, 45)
		ctx.lineTo(18, 45)
	}
}
