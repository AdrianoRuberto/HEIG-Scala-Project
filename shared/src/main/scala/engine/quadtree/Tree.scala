package engine.quadtree

import engine.geometry.{Point, Rectangle}

private[quadtree] class Tree[T: Bounded] (val x: Double, val y: Double, val width: Double, val height: Double)
		extends QuadTree[T] {

	private val midX = x + (width / 2)
	private val midY = y + (height / 2)

	private var objects: Set[T] = Set.empty
	private var split: Boolean = false
	private var children: Array[Tree[T]] = null

	private def collapsible: Boolean = !split && objects.isEmpty

	def insert(obj: T)(implicit bb: BoundingBox): Unit = {
		val box = bb.rect
		require(box.left >= x && box.top >= y && box.right <= x + width && box.bottom <= height,
			"Attempting to insert an object whose bounding box is not contained in the quadtree area")
		lazy val q = quadrant(box)
		if (split && q >= 0) {
			children(q).insert(obj)
		} else {
			objects += obj
			if (!split && objects.size > NodeCapacity) splitNode()
		}
	}

	def remove(obj: T)(implicit bb: BoundingBox): Unit = {
		lazy val q = quadrant(bb.rect)
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

	def query(point: Point): Iterator[T] = {
		lazy val q = quadrant(point)
		val matching = objects.iterator.filter(o => o.boundingBox.rect contains point)
		if (split && q >= 0) matching ++ children(q).query(point)
		else if (split) children.foldLeft(matching)(_ ++ _.query(point))
		else matching
	}

	def query(box: Rectangle): Iterator[T] = {
		lazy val q = quadrant(box)
		val matching = objects.iterator.filter(o => o.boundingBox.rect intersect box)
		if (split && q >= 0) matching ++ children(q).query(box)
		else if (split) children.foldLeft(matching)(_ ++ _.query(box))
		else matching
	}

	def iterator: Iterator[T] = {
		if (split) children.foldLeft(objects.iterator)(_ ++ _.iterator)
		else objects.iterator
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
			new Tree[T](x, y, w_2, h_2), new Tree[T](x + w_2, y, w_2, h_2),
			new Tree[T](x, y + h_2, w_2, h_2), new Tree[T](x + w_2, y + h_2, w_2, h_2)
		)
		for (obj <- objects) quadrant(obj.boundingBox.rect) match {
			case -1 => keptObjects += obj
			case q => children(q).insert(obj)
		}
		split = true
		objects = keptObjects
	}
}
