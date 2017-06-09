package game.client.entities
import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.spells.icons.SpellIcon

class PlayerSpells (x: Double, y: Double,
                    playerSkeleton: CharacterSkeleton,
                    playerSpells: => Array[Option[SpellSkeleton]])
	extends Entity with AbsolutePosition {

	val layer: Layer = Layer.Interface
	val boundingBox: Rectangle = Rectangle(x, y, 300, 105)

	val keys = Seq("M1", "E", "Q", "SHIFT")

	def draw(ctx: CanvasCtx): Unit = {
		ctx.transform(1, 0.05, -0.1, 1, 240, 10)

		ctx.textAlign = "center"
		ctx.textBaseline = "hanging"
		ctx.font = "400 12px 'Roboto Mono'"
		ctx.fillStyle = "#000"

		for ((Some(skeleton), key) <- playerSpells zip keys) {
			SpellIcon.forSpell(skeleton.spell.value).draw(ctx, playerSkeleton, skeleton)
			ctx.fillText(key, 30, 65)
			ctx.translate(-75, 0)
		}
	}
}
