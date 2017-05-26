package engine
package modules

import engine.entity.feature.Drawable
import engine.geometry.Rectangle
import engine.quadtree.QuadTree
import scala.util.Sorting

trait EngineScene {
	this: Engine =>

	/** The set of every drawable entities registered with the engine */
	private[engine] var drawableEntities = QuadTree[Drawable.Key](0, 0, 100, 100)

	/** The set of every absolute-positioned drawable entities */
	private[engine] var absoluteDrawableEntities = Set.empty[Drawable]

	var drawBoundingBoxes: Boolean = false
	var drawWorldBoundingBox: Boolean = true

	/**
	  * Resize the world QuadTree.
	  *
	  * The quad-tree can only contain entities whose bounding box is inside
	  * the quad-tree bounds. Attempting to register an entity with invalid
	  * position will throw an exception.
	  *
	  * When resizing the world while there are still drawable entities
	  * registered with the engine, it must be the case that all of these
	  * entities can still be placed within the bounds of the new quad-tree.
	  * Otherwise an exception is thrown and the operation is aborted.
	  *
	  * @param width  the world width
	  * @param height the world height
	  */
	def setWorldSize(width: Double, height: Double): Unit = {
		val tree = QuadTree[Drawable.Key](0, 0, width, height)
		for (entity <- drawableEntities) tree += entity
		drawableEntities = tree
	}

	/**
	  * Updates the world quad-tree by checking if the bounding box of any
	  * registered entity was updated during the last update and need to
	  * be also updated in the quad-tree index.
	  */
	private[engine] def updateDrawableTree(): Unit = {
		for (entry @ (entity, oldBox) <- drawableEntities; newBox = entity.boundingBox) {
			if (oldBox != newBox) {
				drawableEntities -= entry
				drawableEntities += (entity, newBox)
			}
		}
	}

	/**
	  * Draws visible entities on the canvas.
	  */
	private[engine] def drawVisibleEntities(): Unit = {
		// Update the quad-tree index
		updateDrawableTree()

		// Camera view box
		val view = camera.box

		val visibles = drawableEntities.filter { case (_, box) => view intersect box }.toBuffer
		val absolutes = absoluteDrawableEntities.view.map { e => (e, e.boundingBox) }

		val entities = (visibles ++ absolutes).toArray
		Sorting.quickSort(entities)

		// Draw actors
		ctx.setTransform(1, 0, 0, 1, 0.5, 0.5)
		ctx.clearRect(-1, -1, canvas.width + 2, canvas.height + 2)

		if (drawWorldBoundingBox) {
			ctx.strokeStyle = "blue"
			ctx.strokeRect(drawableEntities.x - view.left, drawableEntities.y - view.top,
				drawableEntities.width, drawableEntities.height)
			ctx.strokeStyle = "black"
		}

		for ((entity, box) <- entities) {
			ctx.save()
			if (entity.positionIsAbsolute) translateAbsolute(ctx, box)
			else ctx.translate(box.left - view.left, box.top - view.top)
			if (drawBoundingBoxes) {
				ctx.strokeStyle = "red"
				ctx.strokeRect(0, 0, box.width, box.height)
				ctx.strokeStyle = "black"
			}
			entity.draw(ctx)
			ctx.restore()
		}
	}

	@inline	private def translateAbsolute(ctx: CanvasCtx, box: Rectangle): Unit = {
		ctx.translate(
			if (box.left < 0) box.left + ctx.canvas.width - box.width else box.left,
			if (box.top < 0) box.top + ctx.canvas.height - box.height else box.top
		)
	}
}
