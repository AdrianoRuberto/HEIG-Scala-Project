package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.spells.icon.SpellIcon

class PlayerSpells (x: Double, y: Double, player: Player) extends Entity with AbsolutePosition {
	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(x, y, 300, 105)

	private val skeleton = player.skeleton

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, 0.05, -0.1, 1, 0, 0)
		//ctx.strokeRect(0, 0, 300, 90)

		ctx.translate(300 - 60, 10)

		val spells = Seq((SpellIcon.Sword, "M1"), (SpellIcon.Sprint, "Shift"), (SpellIcon.Sprint, "Q"), (SpellIcon.Sprint, "F1"))

		ctx.textAlign = "center"
		ctx.textBaseline = "hanging"
		ctx.font = "400 12px 'Roboto Mono'"
		ctx.fillStyle = "#000"

		for ((spell, key) <- spells) {
			spell.draw(ctx)
			ctx.fillText(key, 30, 65)
			ctx.translate(-75, 0)
		}

	}
}
