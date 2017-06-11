package game.skeleton

import game.UID
import game.skeleton.node.{Node, NodeEvent}
import java.util.concurrent.atomic.AtomicInteger

/**
  * A skeleton is an data container that is kept in sync between
  * client and server automatically.
  */
abstract class AbstractSkeleton(tpe: Skeleton[_ <: AbstractSkeleton]) {
	// Should be provided by as case class arguments
	val uid: UID
	val remotes: Iterable[RemoteManagerAgent]

	/** Implicit reference to this skeleton */
	protected implicit val self: this.type = this

	// Notify client-side of this skeleton instantiation
	for (remote <- remotes) remote send ManagerEvent.InstantiateSkeleton(tpe, uid)

	// NodeIds generator
	private val lastNodeId = new AtomicInteger(0)
	private[skeleton] def nextNodeId: NodeId = NodeId(lastNodeId.incrementAndGet())

	// The collection of nodes of this skeleton
	private[skeleton] var nodes: Map[NodeId, Node[_]] = Map.empty

	/** Receives notifications from the server instance of this skeleton */
	final def receive(notification: ManagerEvent.NotifyNode): Unit = nodes.get(notification.nid) match {
		case Some(node) =>
			val span = notification.serial - node.serial
			if (span >= 0) {
				if (span > 0) node.commit(notification.serial)
				node.asInstanceOf[Node[NodeEvent]].receive(notification.event)
			}
		case None => throw new IllegalStateException(s"Received notification for unknown node: ${notification.nid}")
	}

	def collect(): Unit = {
		for (remote <- remotes) remote send ManagerEvent.CollectSkeleton(uid)
	}
}
