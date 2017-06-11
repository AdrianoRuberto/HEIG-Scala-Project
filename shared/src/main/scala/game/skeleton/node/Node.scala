package game.skeleton.node

import game.skeleton.{AbstractSkeleton, ManagerEvent, NodeId}

/**
  * A node is a container for some data inside a skeleton.
  *
  * @param skeleton the owner skeleton
  * @tparam E the type of event received by this node
  */
abstract class Node[E <: NodeEvent](implicit val skeleton: AbstractSkeleton) {
	/** This node ID, unique for a given skeleton */
	val nid: NodeId = skeleton.nextNodeId
	skeleton.nodes += (nid -> this)

	// Serialization
	private var currentSerial: Int = 0
	def serial: Int = currentSerial
	def commit(serial: Int = currentSerial + 1): this.type = {
		currentSerial = serial
		this
	}

	/** Receives a event from the server-side instance of this node */
	def receive(event: E): Unit

	/** Whether this node should send events to remotes skeletons */
	@inline protected final def shouldSend: Boolean = skeleton.remotes.nonEmpty

	/** Transmits an event to the client-side version of this node. */
	@inline protected final def send(event: E): Unit = {
		for (remote <- skeleton.remotes) remote send ManagerEvent.NotifyNode(skeleton.uid, nid, currentSerial, event)
	}

	/** Transmits an event to the client-side version of this node with latency awareness. */
	@inline protected final def sendLatencyAware(f: Double => E): Unit = {
		for (remote <- skeleton.remotes) {
			remote sendLatencyAware (latency => ManagerEvent.NotifyNode(skeleton.uid, nid, currentSerial, f(latency)))
		}
	}
}
