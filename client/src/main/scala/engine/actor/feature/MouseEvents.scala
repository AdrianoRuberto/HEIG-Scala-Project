package engine
package actor
package feature

trait MouseEvents extends Actor {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		engine.mouseEnabled += this
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		super.unregisterFrom(engine)
		engine.mouseEnabled -= this
	}

	def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit
}
