package game.spells.icon

import engine.CanvasCtx
import game.protocol.enums.Spell
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

	def forSpell(spell: Spell): SpellIcon = spell match {
		case Spell.Sprint => Sprint
		case Spell.Sword => Sword
	}
}
