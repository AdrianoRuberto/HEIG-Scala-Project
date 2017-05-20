package engine
package entity

import scala.collection.mutable

abstract class Entity {
	private[this] var owner: Option[Engine] = None
	private[this] var id: Int = -1

	private[engine] def registerWith(engine: Engine): Unit = {
		require(owner.isEmpty, "Attempt to register an already registered entity")
		owner = Some(engine)
		id = engine.entityIdsAllocator.alloc()
	}

	private[engine] def unregisterFrom(engine: Engine): Unit = {
		require(engine == owner.orNull, "Attempt to unregister from foreign engine")
		owner = None
		engine.entityIdsAllocator.free(id)
		id = -1
	}

	def unregister(): Unit = owner match {
		case Some(engine) => unregisterFrom(engine)
		case None => throw new IllegalStateException("Cannot unregister an entity that is not registered")
	}

	def entityId: Int = id
}

object Entity {
	class IdAllocator {
		private var lastEntityId: Int = 0
		private val recycledEntityIds = mutable.Queue.empty[Int]

		private[engine] def alloc(): Int = {
			if (recycledEntityIds.nonEmpty) {
				recycledEntityIds.dequeue()
			} else if (lastEntityId < Int.MaxValue) {
				lastEntityId += 1
				lastEntityId
			} else {
				throw new IllegalStateException("Too many entityIDs have been allocated")
			}
		}

		private[engine] def free(id: Int): Unit = {
			require(id > 0, "Invalid entityID")
			recycledEntityIds.enqueue(id)
		}
	}
}
