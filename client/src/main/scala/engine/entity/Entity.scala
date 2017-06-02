package engine
package entity

import engine.geometry.Rectangle
import engine.quadtree.{Bounded, BoundingBox}
import engine.utils.Layer
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
		attached()
		for (child <- children) child.registerWith(engine)
	}

	/** Called by the engine when the entity is unregistered */
	private[engine] def unregisterFrom(engine: Engine): Unit = {
		require(engine == owner.orNull, "Attempt to unregister from foreign engine")
		engine.entities -= this
		owner = js.undefined
		detached()
		for (child <- children) child.unregister(soft = true)
	}

	/** Unregisters the entity from the engine */
	def unregister(soft: Boolean = false): Unit = owner.orNull match {
		case null => if (!soft) throw new IllegalStateException("Cannot unregister an entity that is not registered")
		case engine => unregisterFrom(engine)
	}

	/** The engine to which this entity is registered */
	def engine: Engine = owner.orNull

	def layer: Layer
	def boundingBox: Rectangle
	def positionIsAbsolute: Boolean = false
	def draw(ctx: CanvasCtx): Unit

	/** Called when the unit is registered with the engine */
	protected def attached(): Unit = ()

	/** Called when the unit is unregistered from the engine */
	protected def detached(): Unit = ()
}

object Entity {
	implicit val drawableOrdering: Ordering[Entity] = Ordering.by(entity => entity.layer)

	type Key = (Entity, Rectangle)

	implicit object KeyIsBounded extends Bounded[Key] {
		def boundingBox(obj: (Entity, Rectangle)): BoundingBox = obj._2
	}

	implicit object KeyOrdering extends Ordering[Key] {
		def compare(x: (Entity, Rectangle), y: (Entity, Rectangle)): Int = drawableOrdering.compare(x._1, y._1)
	}

	implicit object EntityIsBounded extends Bounded[Entity] {
		def boundingBox(entity: Entity): BoundingBox = BoundingBox(entity.boundingBox)
	}
}
