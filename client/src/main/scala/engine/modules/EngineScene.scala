package engine
package modules

import engine.entity.feature.Drawable
import engine.quadtree.QuadTree
import scala.util.Sorting

trait EngineScene {
	this: Engine =>

	/** The set of every drawable entities registered with the engine */
	private[engine] var drawableEntities = QuadTree[Drawable.Key](0, 0, 100, 100)

	/** The set of every absolute-positioned drawable entities */
	private[engine] var absoluteDrawableEntities = Set.empty[Drawable]

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
		println(view)

		val visibles = drawableEntities.filter { case (_, box) => view intersect box }.toBuffer
		val absolutes = absoluteDrawableEntities.view.map { e => (e, e.boundingBox) }

		val entities = (visibles ++ absolutes).toArray
		Sorting.quickSort(entities)

		// Draw actors
		ctx.setTransform(1, 0, 0, 1, 0, 0)
		ctx.clearRect(0, 0, canvas.width, canvas.height)
		for ((entity, box) <- entities) {
			ctx.save()
			if (entity.positionIsAbsolute) ctx.translate(box.left, box.top)
			else ctx.translate(box.left - view.left, box.top - view.top)
			entity.draw(ctx)
			ctx.restore()
		}
	}
}
