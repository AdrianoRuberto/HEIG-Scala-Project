package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.Rectangle
import engine.utils.Layer

class Nameplate (character: Character) extends Entity {
	val layer: Layer = Layer.Nameplates

	def boundingBox: Rectangle = {
		Rectangle(character.skeleton.x.current - 30, character.skeleton.y.current - character.size / 2 - 25, 60, 10)
	}

	def draw(ctx: CanvasCtx): Unit = {
		ctx.font = "400 10px 'Roboto Mono'"
		ctx.textAlign = "center"
		ctx.textBaseline = "hanging"
		ctx.fillStyle = "#999"
		ctx.fillText(character.skeleton.name.value, 30, 0)
	}
}
