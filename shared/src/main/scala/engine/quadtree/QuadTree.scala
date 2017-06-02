package engine.quadtree

import engine.geometry.{Vector, Rectangle}
import scala.language.implicitConversions

abstract class QuadTree[T: Bounded] {
	// Properties
	val x: Double
	val y: Double
	val width: Double
	val height: Double

	/** Inserts an object in the tree */
	def insert(obj: T)(implicit bb: BoundingBox = obj.boundingBox): Unit

	/** Removes an object from the tree */
	def remove(obj: T)(implicit bb: BoundingBox = obj.boundingBox): Unit

	/** Alias for [[insert]] */
	@inline final def += (obj: T)(implicit bb: BoundingBox = obj.boundingBox): Unit = insert(obj)

	/** Alias for [[remove]] */
	@inline final def -= (obj: T)(implicit bb: BoundingBox = obj.boundingBox): Unit = remove(obj)

	def query(point: Vector): Iterator[T]
	def query(box: Rectangle): Iterator[T]

	def iterator: Iterator[T]

	def clear(): Unit
}

object QuadTree {
	def apply[T: Bounded](x: Double, y: Double, width: Double, height: Double): QuadTree[T] =
		new Tree[T](x, y, width, height)

	/** Implicit conversion from [[QuadTree]] to [[Iterator]] for in use for-loops */
	@inline implicit def treeToIterator[T](tree: QuadTree[T]): Iterator[T] = tree.iterator
}
