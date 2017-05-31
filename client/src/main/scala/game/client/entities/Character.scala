package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.Updatable
import engine.geometry.Rectangle
import engine.utils.Layer
import game.skeleton.concrete.CharacterSkeleton

class Character(val skeleton: CharacterSkeleton, sublayer: Int = 0) extends Entity with Updatable {
	children += new Nameplate(this)

	// Drawing layer
	val layer: Layer = Layer.Players / sublayer

	// Bounding box of this character
	def boundingBox = Rectangle(skeleton.x.current - 15, skeleton.y.current - 15, 30, 30)

	private var lastX = 0.0
	private var lastY = 0.0
	private var targetFacing = 0.0
	private var facing = 0.0

	def update(dt: Double): Unit = {
		if (skeleton.facingOverride.value) {
			targetFacing = skeleton.facingDirection.value
		} else if (skeleton.moving.value) {
			val x = skeleton.x.current
			val y = skeleton.y.current
			if (x != lastX || y != lastY) {
				targetFacing = Math.atan2(y - lastY, x - lastX)
				lastX = x
				lastY = y
			}
		}

		if (facing != targetFacing) {
			val delta = Math.atan2(Math.sin(targetFacing - facing), Math.cos(targetFacing - facing))
			if (Math.abs(delta) < 0.01) facing = targetFacing
			else facing += delta / 3
		}
	}

	def draw(ctx: CanvasCtx): Unit = {
		ctx.translate(15, 15)
		ctx.rotate(facing - Math.PI / 2)
		ctx.translate(-15, -15)

		ctx.fillStyle = "#eee"
		ctx.fillRect(0, 0, 30, 30)

		ctx.fillStyle = skeleton.color.value
		ctx.fillRect(0, 30, 30, - 30 * skeleton.health.percent)

		ctx.strokeStyle = "black"
		ctx.fillStyle = "black"

		ctx.strokeRect(0, 0, 30, 30)
		ctx.fillRect(7.5, 20, 4, 4)
		ctx.fillRect(18.5, 20, 4, 4)
	}
}
