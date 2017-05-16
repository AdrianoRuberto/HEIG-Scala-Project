package engine
package actor

abstract class Actor {
	private[this] var owner: Option[Engine] = None

	private[engine] def registerWith(engine: Engine): Unit = {
		owner = Some(engine)
	}

	private[engine] def unregisterFrom(engine: Engine): Unit = {
		require(engine == owner.orNull, "Attempt to unregister from foreign engine")
		owner = None
	}

	def unregister(): Unit = owner match {
		case Some(engine) => unregisterFrom(engine)
		case None => throw new IllegalStateException("Cannot unregister an actor that is not registered")
	}
}
