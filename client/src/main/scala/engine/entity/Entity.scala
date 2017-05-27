package engine
package entity

import scala.scalajs.js

abstract class Entity {
	/** Make the entity itself available as an implicit argument */
	protected implicit val self: this.type = this
	private[this] var owner: js.UndefOr[Engine] = js.undefined

	protected var children: Set[Entity] = Set.empty

	/** Called by the engine when the entity is registered */
	private[engine] def registerWith(engine: Engine): Unit = {
		require(owner.isEmpty, "Attempt to register an already registered entity")
		engine.entities += this
		owner = engine
		for (child <- children) child.registerWith(engine)
	}

	/** Called by the engine when the entity is unregistered */
	private[engine] def unregisterFrom(engine: Engine): Unit = {
		require(engine == owner.orNull, "Attempt to unregister from foreign engine")
		engine.entities -= this
		owner = js.undefined
		for (child <- children) child.unregisterFrom(engine)
	}

	/** Unregisters the entity from the engine */
	def unregister(): Unit = owner.orNull match {
		case null => throw new IllegalStateException("Cannot unregister an entity that is not registered")
		case engine => engine.unregisterEntity(this)
	}

	/** The engine to which this entity is registered */
	def engine: Engine = owner.orNull
}
