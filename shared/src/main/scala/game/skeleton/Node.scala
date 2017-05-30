package game.skeleton

import boopickle.DefaultBasic._
import game.skeleton.Event.{NodeEvent, NotifyNode}
import game.skeleton.Node.NodeId

abstract class Node[E <: NodeEvent](implicit val skeleton: AbstractSkeleton) {
	val nid: NodeId = skeleton.nextNodeId
	skeleton.nodes += (nid -> this)

	def receive(event: E): Unit

	@inline
	final def emit(event: E): Unit = {
		val transmitter = skeleton.transmitter
		if (transmitter != Transmitter.NoTransmitter) {
			transmitter ! NotifyNode(skeleton.uid, nid, event)
		}
	}
}

object Node {
	case class NodeId(value: Int) extends AnyVal
	implicit val nodeIdPickler: Pickler[NodeId] = PicklerGenerator.generatePickler[NodeId]
}
