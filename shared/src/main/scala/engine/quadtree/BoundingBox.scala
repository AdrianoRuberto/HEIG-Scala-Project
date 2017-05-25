package engine.quadtree

import engine.geometry.Rectangle
import scala.language.implicitConversions

/**
  * A marker type for QuadTree bounding boxes.
  *
  * It is used as an implicit parameter to methods [[QuadTree.insert]] and
  * [[QuadTree.remove]] to prevent multiple computations of the bounding box
  * on recursive calls to lower levels.
  *
  * The top-level call will be missing its implicit parameter and will trigger
  * the default value that will generate a new [[BoundingBox]] by using the
  * context-bound [[Bounded]] instance. Recursive calls will be able to reuse
  * this instance all the way.
  *
  * @param rect the bounding box rectangle
  */
case class BoundingBox (rect: Rectangle) extends AnyVal

object BoundingBox {
	/** Implicitly wraps a [[Rectangle]] inside a [[BoundingBox]]. This is a no-op. */
	@inline implicit def wrap(rectangle: Rectangle): BoundingBox = BoundingBox(rectangle)
}
