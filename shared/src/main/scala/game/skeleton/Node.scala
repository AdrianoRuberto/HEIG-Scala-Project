package game.skeleton

import boopickle.DefaultBasic._
import java.nio.ByteBuffer

class Node[T: Pickler] private (val nid: Int, private var current: T, skeleton: AbstractSkeleton) {
	def value: T = current

	def value_= (newValue: T): Unit = {
		current = newValue
		// Transmit update
		val buffer = Pickle.intoBytes(newValue)
		val array = new Array[Byte](buffer.remaining)
		buffer.get(array)
		skeleton emit Event.NodeUpdate(nid, array)
	}

	private[skeleton] def value_= (pickled: Array[Byte]): Unit = {
		value = Unpickle[T].fromBytes(ByteBuffer.wrap(pickled))
	}
}

object Node {
	def apply[T: Pickler](initialValue: T)(implicit skeleton: AbstractSkeleton): Node[T] = {
		val nid = skeleton.lastNodeId.incrementAndGet()
		val node = new Node(nid, initialValue, skeleton)
		skeleton.nodes += (nid -> node)
		node
	}
}
