package game.skeleton

import game.UID
import game.protocol.enums.SkeletonType
import game.skeleton.node.{Node, NodeEvent, NodeId}
import java.util.concurrent.atomic.AtomicInteger

/**
  * A skeleton is an data container that is kept in sync between
  * client and server automatically.
  */
class AbstractSkeleton(tpe: SkeletonType, val uid: UID = UID.next)
                      (implicit val transmitter: Transmitter = Transmitter.NoTransmitter) {
	protected implicit val self: this.type = this

	// Notify client-side of this skeleton instantiation
	if (transmitter != Transmitter.NoTransmitter) {
		transmitter ! ManagerEvent.InstantiateSkeleton(tpe, uid)
	}

	// NodeIds generator
	private val lastNodeId = new AtomicInteger(0)
	private[skeleton] def nextNodeId: NodeId = NodeId(lastNodeId.incrementAndGet())

	// The collection of nodes of this skeleton
	private[skeleton] var nodes: Map[NodeId, Node[_]] = Map.empty

	/** Receives notifications from the server instance of this skeleton */
	final def receive(notification: ManagerEvent.NotifyNode): Unit = nodes.get(notification.nid) match {
		case Some(node) => node.asInstanceOf[Node[NodeEvent]].receive(notification.event)
		case None => throw new IllegalStateException(s"Received notification for unknown node: ${notification.nid}")
	}
}
