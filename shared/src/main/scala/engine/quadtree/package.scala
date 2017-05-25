package engine

package object quadtree {
	/**
	  * Implicit operations on objects for which a [[Bounded]] instance is available.
	  */
	private[quadtree] implicit final class BoundedOps[T](private val obj: T) extends AnyVal {
		/** Returns the [[BoundingBox]] of the object, using the context-bound [[Bounded]] */
		@inline def boundingBox(implicit bounded: Bounded[T]): BoundingBox = bounded.boundingBox(obj)
	}

	/** Minimum node capacity before attempting a split */
	private[quadtree] final val NodeCapacity = 16
}
