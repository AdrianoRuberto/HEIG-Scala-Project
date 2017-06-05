package game.doodads.debug

import engine.CanvasCtx
import engine.entity.Entity
import engine.geometry.{Circle, Rectangle}
import engine.utils.Layer
import game.skeleton.concrete.PointSkeleton

class PointEntity (skeleton: PointSkeleton) extends Entity {
	val layer: Layer = Layer.Debug
	def boundingBox: Rectangle = Circle(skeleton.x.current, skeleton.y.current, 4).boundingBox
	def draw(ctx: CanvasCtx): Unit = {
		ctx.arc(4, 4, 4, 0, 2 * Math.PI)
		ctx.fillStyle = skeleton.color.value
		ctx.fill()
		ctx.strokeStyle = "white"
		ctx.stroke()
	}
}
