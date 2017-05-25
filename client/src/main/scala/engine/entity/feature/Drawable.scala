package engine
package entity
package feature

import engine.geometry.Rectangle
import engine.quadtree.{Bounded, BoundingBox}
import engine.utils.Layer
import scala.language.implicitConversions

trait Drawable extends Entity with Position {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		if (positionIsAbsolute) engine.absoluteDrawableEntities += this
		else engine.drawableEntities += (this, boundingBox)
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		super.unregisterFrom(engine)
		if (positionIsAbsolute) engine.absoluteDrawableEntities -= this
		else engine.drawableEntities -= (this, boundingBox)
	}

	def layer: Layer
	def draw(ctx: CanvasCtx): Unit
}

object Drawable {
	implicit val drawableOrdering: Ordering[Drawable] = Ordering.by(entity => entity.layer)

	type Key = (Drawable, Rectangle)

	implicit object KeyIsBounded extends Bounded[Key] {
		def boundingBox(obj: (Drawable, Rectangle)): BoundingBox = obj._2
	}

	implicit object KeyOrdering extends Ordering[Key] {
		def compare(x: (Drawable, Rectangle), y: (Drawable, Rectangle)): Int = drawableOrdering.compare(x._1, y._1)
	}
}
