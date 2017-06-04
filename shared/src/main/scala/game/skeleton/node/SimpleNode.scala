package game.skeleton.node

import boopickle.DefaultBasic._
import game.skeleton.AbstractSkeleton

/**
  * A simple node holds a single value of type [[T]].
  */
case class SimpleNode[T: Pickler] (private var current: T)
                                  (implicit skeleton: AbstractSkeleton) extends Node[NodeEvent.SimpleEvent] {
	/** Current value of the node */
	def value: T = current

	/** Updates the value of this node */
	def set(newValue: T, force: Boolean = false): Unit = if (newValue != value || force) {
		current = newValue
		if (shouldSend) {
			this send NodeEvent.SimpleUpdate(pickle(newValue))
		}
	}

	/** Alias for set */
	def value_=(newValue: T): Unit = set(newValue)

	/** Handle reception of update events from server */
	override def receive(event: NodeEvent.SimpleEvent): Unit = event match {
		case NodeEvent.SimpleUpdate(buffer) => value = unpickle(buffer)
	}
}
