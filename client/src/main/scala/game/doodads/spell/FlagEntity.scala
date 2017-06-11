package game.doodads.spell

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.{Rectangle, Vector2D}
import engine.utils.Layer
import game.skeleton.core.CharacterSkeleton
import game.spells.icons.DropTheFlag
import utils.Color

class FlagEntity (location: Vector2D, holder: Option[CharacterSkeleton], color: Color) extends Entity {
	val layer: Layer = Layer.HighFx

	private var lastLocation: Vector2D = Vector2D.zero
	private var lastLocationUpdate: Double = 0.0

	def boundingBox: Rectangle = {
		val currentLocation = holder.map(_.position).getOrElse(location)
		Rectangle(currentLocation.x - 2, currentLocation.y - 50, 40, 50)
	}

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillStyle = "#666"
		ctx.fillRect(1, 20, 3, 30)
		ctx.translate(-24, -13)
		ctx.scale(1.4, 1.4)
		DropTheFlag.drawFlag(ctx, Some(color))
		ctx.lineWidth = 1 / 1.4
		ctx.strokeStyle = "#333"
		ctx.stroke()
	}
}
