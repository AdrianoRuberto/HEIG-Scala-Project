package game.skeleton

import boopickle.Default._
import game.UID
import game.protocol.enums.SkeletonType
import game.skeleton.node.{NodeEvent, NodeId}

sealed trait ManagerEvent

object ManagerEvent {
	/** Instantiates a new skeleton of the given type with a given UID */
	case class InstantiateSkeleton(tpe: SkeletonType, uid: UID) extends ManagerEvent

	/** Notify a node of the given skeleton about an event */
	case class NotifyNode(uid: UID, nid: NodeId, event: NodeEvent) extends ManagerEvent

	private implicit val UIDPickler = UID.pickler
	private implicit val NodeIdPickler = NodeId.pickler
	implicit val pickler: Pickler[ManagerEvent] = generatePickler[ManagerEvent]
}
