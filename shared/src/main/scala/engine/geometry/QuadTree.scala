package engine.geometry

import engine.geometry.QuadTree.{Bounded, BoundedOps}

class QuadTree[T: Bounded] private (x: Double, y: Double, width: Double, height: Double, limit: Int = 10) {

	private val midX = x + (width / 2)
	private val midY = y + (height / 2)

	private var objects: Set[T] = Set.empty
	private var split: Boolean = false
	private var children: Array[QuadTree[T]] = null

	private def collapsible: Boolean = !split && objects.isEmpty

	def insert(obj: T): Unit = {
		lazy val q = quadrant(obj.boundingBox)
		if (split && q >= 0) {
			children(q).insert(obj)
		} else {
			objects += obj
			if (!split && objects.size > limit) splitNode()
		}
	}

	def remove(obj: T): Unit = {
		lazy val q = quadrant(obj.boundingBox)
		if (split && q >= 0) {
			children(q).remove(obj)
			if (children.forall(_.collapsible)) {
				split = false
				children = null
			}
		} else {
			objects -= obj
		}
	}

	def query(point: Point): Set[T] = {
		lazy val q = quadrant(point)
		val matching = objects.filter(o => o.boundingBox contains point)
		if (split && q >= 0) matching ++ children(q).query(point)
		else if (split) children.foldLeft(matching)(_ ++ _.query(point))
		else matching
	}

	def query(box: Rectangle): Set[T] = {
		lazy val q = quadrant(box)
		val matching = objects.filter(o => o.boundingBox intersect box)
		if (split && q >= 0) matching ++ children(q).query(box)
		else if (split) children.foldLeft(matching)(_ ++ _.query(box))
		else matching
	}

	def clear(): Unit = {
		objects = Set.empty
		split = false
		children = null
	}

	@inline
	private def quadrant(point: Point): Int = {
		quadrant(top = point.y < midY, bottom = point.y > midY, left = point.x < midX, right = point.x > midX)
	}

	@inline
	private def quadrant(box: Rectangle): Int = {
		quadrant(top = box.bottom < midY, bottom = box.top > midY, left = box.right < midX, right = box.left > midX)
	}

	private def quadrant(top: Boolean, bottom: Boolean, left: Boolean, right: Boolean): Int = {
		(top, bottom, left, right) match {
			case (true, false, true, false) => 0 // Top Left
			case (true, false, false, true) => 1 // Top Right
			case (false, true, true, false) => 2 // Bottom Left
			case (false, true, false, true) => 3 // Bottom Right
			case _ => -1
		}
	}

	private def splitNode(): Unit = {
		var keptObjects = Set.empty[T]
		val w_2 = width / 2
		val h_2 = height / 2
		children = Array(
			QuadTree[T](x, y, w_2, h_2, limit), QuadTree[T](x + w_2, y, w_2, h_2, limit),
			QuadTree[T](x, y + h_2, w_2, h_2, limit), QuadTree[T](x + w_2, y + h_2, w_2, h_2, limit)
		)
		for (obj <- objects) quadrant(obj.boundingBox) match {
			case -1 => keptObjects += obj
			case q => children(q).insert(obj)
		}
		split = true
		objects = keptObjects
	}
}

object QuadTree {
	@inline def apply[T: Bounded](x: Double, y: Double, width: Double, height: Double, limit: Int = 10): QuadTree[T] = {
		new QuadTree[T](x, y, width, height, limit)
	}

	@inline def fromRect[T: Bounded](r: Rectangle, limit: Int = 10): QuadTree[T] = {
		apply(r.x, r.y, r.width, r.height, limit)
	}

	trait Bounded[-T] {
		def boundingBox(obj: T): Rectangle
	}

	implicit final class BoundedOps[T](private val obj: T) extends AnyVal {
		@inline def boundingBox(implicit bounded: Bounded[T]): Rectangle = bounded.boundingBox(obj)
	}
}
