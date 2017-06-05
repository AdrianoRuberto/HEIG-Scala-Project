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
			if (box.left < 0) box.left + ctx.canvas.width - box.width else box.left,
			if (box.top < 0) box.top + ctx.canvas.height - box.height else box.top
		)
	}
}
