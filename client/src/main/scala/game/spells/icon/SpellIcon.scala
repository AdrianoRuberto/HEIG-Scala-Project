package game.spells.icon

import engine.CanvasCtx
import game.spells.icon.SpellIcon._

trait SpellIcon {
	final def draw(ctx: CanvasCtx): Unit = {
		ctx.translate(2, 2)
		ctx.beginPath()
		ctx.fillStyle = "rgba(17, 17, 17, 0.2)"
		drawButton(ctx)
		ctx.fill()
		ctx.translate(-2, -2)

		ctx.beginPath()
		ctx.fillStyle = "rgba(255, 255, 255, 0.9)"
		ctx.strokeStyle = "rgba(17, 17, 17, 0.3)"
		drawButton(ctx)
		ctx.fill()
		ctx.stroke()

		ctx.beginPath()
		ctx.fillStyle = "rgba(17, 17, 17, 0.8)"
		drawIcon(ctx)
		ctx.fill()
	}

	private def drawButton(ctx: CanvasCtx): Unit = {
		ctx.moveTo(buttonRadius, 0)
		ctx.lineTo(buttonSize - buttonRadius, 0)
		ctx.arc(buttonSize - buttonRadius, buttonRadius, buttonRadius, -Math.PI / 2, 0)
		ctx.lineTo(buttonSize, buttonSize - buttonRadius)
		ctx.arc(buttonSize - buttonRadius, buttonSize - buttonRadius, buttonRadius, 0, Math.PI / 2)
		ctx.lineTo(buttonRadius, buttonSize)
		ctx.arc(buttonRadius, buttonSize - buttonRadius, buttonRadius, Math.PI / 2, Math.PI)
		ctx.lineTo(0, buttonRadius)
		ctx.arc(buttonRadius, buttonRadius, buttonRadius, Math.PI, -Math.PI / 2)
	}

	protected def drawIcon(ctx: CanvasCtx): Unit
}

object SpellIcon {
	final val buttonSize = 60
	final val buttonRadius = 10

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

	object Sword extends SpellIcon {
		def drawIcon(ctx: CanvasCtx): Unit = {
			ctx.save()
			ctx.translate(10, 10)
			ctx.scale(2.0/3.0, 2.0/3.0)

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
}
