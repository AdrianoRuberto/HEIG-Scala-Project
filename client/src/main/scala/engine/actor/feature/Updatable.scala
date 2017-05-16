package engine
package actor
package feature

trait Updatable extends Actor {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		engine.updatables += this
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		super.unregisterFrom(engine)
		engine.updatables -= this
	}

	def update(dt: Double): Unit
}
