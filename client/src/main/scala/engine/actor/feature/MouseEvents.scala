package engine
package actor
package feature

trait MouseEvents extends Actor {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		engine.mouseEnabled += this
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		engine.mouseEnabled -= this
		super.unregisterFrom(engine)
	}

	def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit
}
