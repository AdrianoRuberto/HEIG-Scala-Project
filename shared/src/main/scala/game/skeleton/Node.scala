package game.skeleton

/**
  * A node is a container for some data inside a skeleton.
  *
  * @param skeleton the owner skeleton
  * @tparam E the type of event received by this node
  */
abstract class Node[E <: Event.NodeEvent](implicit val skeleton: AbstractSkeleton) {
	/** This node ID, unique for a given skeleton */
	val nid: NodeId = skeleton.nextNodeId
	skeleton.nodes += (nid -> this)

	/** Receives a event from the server-side instance of this node */
	def receive(event: E): Unit

	/** Transmits an event to the client-side version of this node. */
	@inline final def emit(event: E): Unit = {
		val transmitter = skeleton.transmitter
		if (transmitter != Transmitter.NoTransmitter) {
			transmitter ! Event.NotifyNode(skeleton.uid, nid, event)
		}
	}
}
