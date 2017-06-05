package game.skeleton.node

import boopickle.DefaultBasic._
import game.skeleton.AbstractSkeleton
import game.skeleton.node.NodeEvent.SimpleEvent

/**
  * A simple node holds a single value of type [[T]].
  */
abstract class SimpleNode[T] (private var current: T)
                             (implicit skeleton: AbstractSkeleton) extends Node[NodeEvent.SimpleEvent] {
	/** Current value of the node */
	def value: T = current

	/** Updates the value of this node */
	def set(newValue: T, force: Boolean = false): Unit = if (newValue != value || force) {
		current = newValue
		if (shouldSend) send(newValue)
	}

	/** Alias for set */
	def value_=(newValue: T): Unit = set(newValue)

	/** Send update to client-side */
	protected def send(value: T): Unit
}

object SimpleNode {
	def apply(value: Boolean)(implicit skeleton: AbstractSkeleton): SimpleNode[Boolean] = SimpleNodeBoolean(value)
	def apply(value: Int)(implicit skeleton: AbstractSkeleton): SimpleNode[Int] = SimpleNodeInt(value)
	def apply(value: Double)(implicit skeleton: AbstractSkeleton): SimpleNode[Double] = SimpleNodeDouble(value)
	def apply(value: String)(implicit skeleton: AbstractSkeleton): SimpleNode[String] = SimpleNodeString(value)
	def apply[T: Pickler](value: T)(implicit skeleton: AbstractSkeleton): SimpleNode[T] = SimpleNodeGeneric(value)

	private abstract class SpecializedSimpleNode[T] (c: T, event: T => SimpleEvent)
	                                                (implicit s: AbstractSkeleton) extends SimpleNode[T](c) {

		protected def send(value: T): Unit = this send event(value)

		def receive(event: NodeEvent.SimpleEvent): Unit = event match {
			case p: Product => value = p.productElement(0).asInstanceOf[T]
		}
	}

	private case class SimpleNodeBoolean(c: Boolean)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateBoolean)

	private case class SimpleNodeInt(c: Int)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateInt)

	private case class SimpleNodeDouble(c: Double)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateDouble)

	private case class SimpleNodeString(c: String)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateString)

	private case class SimpleNodeGeneric[T: Pickler] (c: T)(implicit s: AbstractSkeleton) extends SimpleNode[T](c) {
		protected def send(value: T): Unit = this send NodeEvent.SimpleUpdateGeneric(pickle(value))
		def receive(event: NodeEvent.SimpleEvent): Unit = event match {
			case NodeEvent.SimpleUpdateGeneric(buffer) => value = unpickle(buffer)
			case _ => throw new IllegalArgumentException("Invalid event for SimpleNodeGeneric.receive")
		}
	}
}
