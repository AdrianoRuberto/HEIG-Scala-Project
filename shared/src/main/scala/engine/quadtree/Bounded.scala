package engine.quadtree

/**
  * A type-class indicating that a [[BoundingBox]] can be computed for
  * an instance of the type [[T]]
  */
trait Bounded[-T] {
	/** Computes the bounding box for the given object */
	def boundingBox(obj: T): BoundingBox
}

