package game.skeleton

import boopickle.DefaultBasic._

/**
  * A simple node holds a single value of type [[T]].
  */
case class SimpleNode[T: Pickler] (private var current: T)
                                  (implicit skeleton: AbstractSkeleton) extends Node[Event.SimpleNodeEvent] {
	/** Current value of the node */
	def value: T = current

	/** Updates the value of this node */
	def value_=(newValue: T): Unit = {
		current = newValue
		if (shouldEmit) {
			this emit Event.SimpleUpdate(pickle(newValue))
		}
	}

	/** Handle reception of update events from server */
	override def receive(event: Event.SimpleNodeEvent): Unit = event match {
		case Event.SimpleUpdate(buffer) => value = unpickle(buffer)
	}
}
