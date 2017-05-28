package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.Drawable
import engine.geometry.Rectangle
import engine.utils.Layer

class Nameplate (character: Character) extends Entity with Drawable {
	val layer: Layer = Layer.Nameplates

	def boundingBox: Rectangle = {
		Rectangle(character.x - 30, character.y - character.size / 2 - 25, 60, 10)
	}

	def draw(ctx: CanvasCtx): Unit = {
		ctx.font = "400 10px 'Roboto Mono'"
		ctx.textAlign = "center"
		ctx.textBaseline = "hanging"
		ctx.fillStyle = "#999"
		ctx.fillText(character.name, 30, 0)
	}
}
