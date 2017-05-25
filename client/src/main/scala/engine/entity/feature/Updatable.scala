package engine
package entity
package feature

trait Updatable extends Entity {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		engine.updatableEntities += this
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		super.unregisterFrom(engine)
		engine.updatableEntities -= this
	}

	def update(dt: Double): Unit
}
