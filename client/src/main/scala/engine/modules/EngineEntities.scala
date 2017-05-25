package engine
package modules

import engine.entity.{Entity, feature}
import scala.collection.mutable

trait EngineEntities { this: Engine =>
	private[engine] val entityIdsAllocator = new Entity.IdAllocator
	private[engine] val updatableEntities = mutable.Set.empty[Entity with feature.Updatable]

	def registerEntity(entity: Entity): Unit = entity.registerWith(this)
	def unregisterEntity(entity: Entity): Unit = entity.unregisterFrom(this)
}
