package game.spells.icons

import engine.CanvasCtx
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.spells.Spell
import game.spells.icons.SpellIcon._

trait SpellIcon {
	final def draw(ctx: CanvasCtx, player: CharacterSkeleton, skeleton: SpellSkeleton): Unit = {
		val activated = skeleton.activated.value
		val ready = skeleton.cooldown.ready
		val available = skeleton.spell.value.cost.forall(player.energy.current >= _)

		ctx.translate(2, 2)
		if (!activated) {
			ctx.beginPath()
			ctx.fillStyle = "rgba(17, 17, 17, 0.2)"
			drawButton(ctx)
			ctx.fill()
			ctx.translate(-2, -2)
		}

		ctx.beginPath()
		drawButton(ctx)
		ctx.fillStyle =
			if (activated) "rgba(255, 240, 191, 0.9)"
			else if (!available) "rgba(230, 200, 200, 0.9)"
			else "rgba(255, 255, 255, 0.9)"
		ctx.fill()
		if (!ready) {
			ctx.save()
			ctx.clip()
			ctx.fillStyle = "rgba(17, 17, 17, 0.5)"
			ctx.fillRect(0, 0, 60, 60 * (1 - skeleton.cooldown.progress))
			ctx.restore()
		}
		ctx.strokeStyle = "rgba(17, 17, 17, 0.3)"
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
