package engine
package actor
package feature

trait KeyboardEvents extends Actor {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		engine.keyboardEnabled += this
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		engine.keyboardEnabled -= this
		super.unregisterFrom(engine)
	}

	def handleKeyboard(tpe: String): Unit
}
