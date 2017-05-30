package game.skeleton

import game.UID
import game.skeleton.Event.{InstantiateSkeleton, NodeEvent, NotifyNode}
import game.skeleton.Node.NodeId
import java.util.concurrent.atomic.AtomicInteger

/**
  * A skeleton is an data container that is kept in sync between
  * client and server automatically.
  */
class AbstractSkeleton(tpe: Type, val uid: UID = UID.next)
                      (implicit val transmitter: Transmitter = Transmitter.NoTransmitter) {
	protected implicit val self: this.type = this
	transmitter ! InstantiateSkeleton(tpe, uid)

	private val lastNodeId = new AtomicInteger(0)
	private[skeleton] def nextNodeId: NodeId = NodeId(lastNodeId.incrementAndGet())
	private[skeleton] var nodes: Map[NodeId, Node[_]] = Map.empty

	final def receive(notification: NotifyNode): Unit = nodes.get(notification.nid) match {
		case Some(node) => node.asInstanceOf[Node[NodeEvent]].receive(notification.event)
		case None => throw new IllegalStateException(s"Received notification for unknown node: $nid")
	}
}
