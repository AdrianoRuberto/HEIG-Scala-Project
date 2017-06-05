package engine
package modules

import engine.entity.Entity
import engine.geometry.Rectangle
import scala.util.Sorting

trait EngineScene {
	this: Engine =>

	var drawBoundingBoxes: Boolean = false

	/**
	  * Draws visible entities on the canvas.
	  */
	private[engine] def drawVisibleEntities(): Unit = {
		// Camera view box
		val view = camera.box

		// Reset canvas
		ctx.setTransform(1, 0, 0, 1, 0.5, 0.5)
		ctx.clearRect(-1, -1, canvas.width + 2, canvas.height + 2)

		for ((entity, box) <- visibleEntities(view)) {
			ctx.save()
			if (entity.positionIsAbsolute) translateAbsolute(ctx, box)
			else ctx.translate(box.left - view.left, box.top - view.top)
			if (drawBoundingBoxes) {
				ctx.strokeStyle = "red"
				ctx.strokeRect(0, 0, box.width, box.height)
				ctx.strokeStyle = "black"
			}
			ctx.beginPath()
			entity.draw(ctx)
			ctx.restore()
		}
	}

	private def visibleEntities(view: Rectangle): Array[Entity.Key] = {
		val visible =
			entities.view
				.map { e => (e, e.boundingBox) }
				.filter { case (e, b) => e.positionIsAbsolute || (b intersect view) }.toArray
		Sorting.quickSort(visible)
		visible
	}

	@inline	private def translateAbsolute(ctx: CanvasCtx, box: Rectangle): Unit = {
		ctx.translate(
			box.left match {
				case x if x.isNaN => (ctx.canvas.width - box.width) / 2
				case x if x < 0 => x + ctx.canvas.width - box.width
				case x => x
			},
			box.top match {
				case y if y.isNaN => (ctx.canvas.height - box.height) / 2
				case y if y < 0 => y + ctx.canvas.height - box.height
				case y => y
			}
		)
	}
}
