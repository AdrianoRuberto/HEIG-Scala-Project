package game.skeleton

import game.UID
import java.util.concurrent.atomic.AtomicInteger

/**
  * A skeleton is an data container that is kept in sync between
  * client and server automatically.
  */
class AbstractSkeleton(tpe: Type, val uid: UID = UID.next)
                      (implicit val receiver: Transmitter = Transmitter.NoTransmitter) {
	protected implicit val self: this.type = this
	this emit Event.InstantiateSkeleton(tpe, uid)

	private[skeleton] val lastNodeId = new AtomicInteger(0)
	private[skeleton] var nodes: Map[Int, Node[_]] = Map.empty

	final def receive(event: Event.SkeletonEvent): Unit = event match {
		case Event.NodeUpdate(nid, value) =>
			nodes.get(nid) match {
				case Some(node) => node.value = value
				case None => throw new IllegalStateException(s"Received update for unknown node: $nid")
			}
	}

	// `event` evaluation should be dead-code on Scala.js side
	@inline final def emit(event: => Event.SkeletonEvent): Unit = {
		if (receiver != Transmitter.NoTransmitter) receiver ! (uid, event)
	}

	// `event` evaluation should be dead-code on Scala.js side
	@inline final def emit(event: => Event.ClosetEvent)(implicit dummyImplicit: DummyImplicit): Unit = {
		if (receiver != Transmitter.NoTransmitter) receiver ! event
	}
}
