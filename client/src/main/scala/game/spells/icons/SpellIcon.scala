package game.spells.icons

import engine.CanvasCtx
import game.protocol.enums.Spell
import game.skeleton.concrete.SpellSkeleton
import game.spells.icons.SpellIcon._

trait SpellIcon {
	final def draw(ctx: CanvasCtx, skeleton: SpellSkeleton): Unit = {
		val activated = skeleton.activated.value
		val ready = skeleton.cooldown.ready

		ctx.translate(2, 2)
		if (!activated) {
			ctx.beginPath()
			ctx.fillStyle = "rgba(17, 17, 17, 0.2)"
			drawButton(ctx)
			ctx.fill()
			ctx.translate(-2, -2)
		}

		ctx.beginPath()
		ctx.fillStyle =
			if (activated) "rgba(255, 240, 191, 0.9)"
			else "rgba(255, 255, 255, 0.9)"
		ctx.strokeStyle = "rgba(17, 17, 17, 0.3)"
		drawButton(ctx)
		ctx.save()
		ctx.clip()
		ctx.fillRect(0, 60, 60, -60 * skeleton.cooldown.progress)
		ctx.restore()
		ctx.stroke()

		ctx.beginPath()
		ctx.fillStyle = "rgba(17, 17, 17, 0.8)"
		drawIcon(ctx)
		ctx.fill()

		if (activated) ctx.translate(-2, -2)
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
