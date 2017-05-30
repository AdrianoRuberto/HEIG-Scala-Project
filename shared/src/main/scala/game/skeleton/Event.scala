package game.skeleton

import boopickle.Default._
import game.UID
import game.skeleton.Node.NodeId

object Event {
	sealed trait NodeEvent
	sealed trait SimpleNodeEvent extends NodeEvent
	case class SimpleUpdate(value: Array[Byte]) extends SimpleNodeEvent

	sealed trait ClosetEvent
	case class InstantiateSkeleton(tpe: Type, uid: UID) extends ClosetEvent
	case class NotifyNode(uid: UID, nid: NodeId, event: NodeEvent) extends ClosetEvent

	private implicit val UIDPickler = UID.pickler
	private implicit val nodeIdPickler = Node.nodeIdPickler
	implicit val skeletonEventPickler: Pickler[NodeEvent] = generatePickler[NodeEvent]
	implicit val closetEventPickler: Pickler[ClosetEvent] = generatePickler[ClosetEvent]
}
