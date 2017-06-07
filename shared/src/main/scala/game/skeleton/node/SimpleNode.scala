package game.skeleton.node

import boopickle.DefaultBasic._
import game.UID
import game.skeleton.AbstractSkeleton
import game.skeleton.node.NodeEvent.SimpleEvent
import java.nio.ByteBuffer
import utils.Color

/**
  * A simple node holds a single value of type [[T]].
  *
  * Specialized version of this node type exists for types: Boolean, Int, Double,
  * String and UID. Specialized versions do not require pickling. If any other
  * type is used as a value of the SimpleNode, the value will require pickling
  * to/from a bytes array before notification of the client-side view of this node.
  */
abstract class SimpleNode[T] (private var current: T)
                             (implicit skeleton: AbstractSkeleton) extends Node[NodeEvent.SimpleEvent] {
	/** Current value of the node */
	def value: T = current

	/**
	  * Updates the value of this node.
	  *
	  * A notification is sent to the remote node only if the value being set is not
	  * equal to the current value of the node. Unless force is `true`, in which case
	  * a notification is always sent.
	  *
	  * @param value the new value of this node
	  * @param force whether a notification should be sent even if the value did not change
	  */
	def set(value: T, force: Boolean = false): Unit = if (value != value || force) {
		current = value
		if (shouldSend) send(value)
	}

	/** Alias for set */
	def value_=(value: T): Unit = set(value)

	/**
	  * Sends updates to client-side.
	  *
	  * This method is abstract since the actual notification object will be chosen
	  * by the implementation class. Specialized nodes use a dedicated message for
	  * their data-type whereas pickled nodes share a single message type using a
	  * bytes array.
	  *
	  * @param value the new value of this node
	  */
	protected def send(value: T): Unit
}

object SimpleNode {
	def apply(value: Boolean)(implicit skeleton: AbstractSkeleton): SimpleNode[Boolean] = SimpleNodeBoolean(value)
	def apply(value: Int)(implicit skeleton: AbstractSkeleton): SimpleNode[Int] = SimpleNodeInt(value)
	def apply(value: Double)(implicit skeleton: AbstractSkeleton): SimpleNode[Double] = SimpleNodeDouble(value)
	def apply(value: String)(implicit skeleton: AbstractSkeleton): SimpleNode[String] = SimpleNodeString(value)
	def apply(value: UID)(implicit skeleton: AbstractSkeleton, dummyImplicit: DummyImplicit): SimpleNode[UID] = SimpleNodeUID(value)
	def apply(value: Color)(implicit skeleton: AbstractSkeleton): SimpleNode[Color] = SimpleNodeColor(value)
	def apply[T: Pickler](value: T)(implicit skeleton: AbstractSkeleton): SimpleNode[T] = SimpleNodeGeneric(value)

	/**
	  * Common structure for specialized simple nodes.
	  *
	  * @param c     the node initial value
	  * @param event the constructor of the update message for this node
	  * @param s     the skeleton containing the node
	  * @tparam T the type of value in this node
	  */
	private abstract class SpecializedSimpleNode[T] (c: T, event: T => SimpleEvent)
	                                                (implicit s: AbstractSkeleton) extends SimpleNode[T](c) {
		/** Sends a notification message using the event factory given by sub class */
		protected def send(value: T): Unit = this send event(value)

		/**
		  * Shared handling of notification messages.
		  *
		  * This implementation is unsafe, but can be trivially shared between
		  * specialized sub classes.
		  *
		  * @param event the received event message
		  */
		def receive(event: NodeEvent.SimpleEvent): Unit = event match {
			case p: Product => value = p.productElement(0).asInstanceOf[T]
		}
	}

	/** The version of SimpleNode specialized for Boolean values */
	private case class SimpleNodeBoolean(c: Boolean)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateBoolean.apply)

	/** The version of SimpleNode specialized for Int values */
	private case class SimpleNodeInt(c: Int)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateInt.apply)

	/** The version of SimpleNode specialized for Double values */
	private case class SimpleNodeDouble(c: Double)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateDouble.apply)

	/** The version of SimpleNode specialized for String values */
	private case class SimpleNodeString(c: String)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateString.apply)

	/** The version of SimpleNode specialized for UID values */
	private case class SimpleNodeUID(c: UID)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateUID.apply)

	/** The version of SimpleNode specialized for Color values */
	private case class SimpleNodeColor(c: Color)(implicit s: AbstractSkeleton)
		extends SpecializedSimpleNode(c, NodeEvent.SimpleUpdateColor.apply)

	/** A generic version of SimpleNode using Pickler to encode and decode values */
	private case class SimpleNodeGeneric[T: Pickler] (c: T)(implicit s: AbstractSkeleton) extends SimpleNode[T](c) {
		protected def send(value: T): Unit = this send NodeEvent.SimpleUpdateGeneric(pickle(value))

		def receive(event: NodeEvent.SimpleEvent): Unit = event match {
			case NodeEvent.SimpleUpdateGeneric(buffer) => value = unpickle(buffer)
			case _ => throw new IllegalArgumentException("Invalid event for SimpleNodeGeneric.receive")
		}

		/** Pickles a value of type T into an array of bytes. */
		private def pickle(value: T): Array[Byte] = {
			val buffer = Pickle.intoBytes(value)
			val array = new Array[Byte](buffer.remaining)
			buffer.get(array)
			array
		}

		/** Unpickles a value of type T from an array of bytes. */
		private def unpickle(buffer: Array[Byte]): T = {
			val value = Unpickle[T].fromBytes(ByteBuffer.wrap(buffer))
			println("received", value)
			value
		}
	}
}
