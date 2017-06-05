package game.skeleton

import game.UID
import game.skeleton.node.{NodeEvent, NodeId}

sealed trait ManagerEvent

object ManagerEvent {
	SkeletonType.Character

	/** Instantiates a new skeleton of the given type with a given UID */
	case class InstantiateSkeleton(tpe: SkeletonType[_ <: AbstractSkeleton], uid: UID) extends ManagerEvent
	case class CollectSkeleton(uid: UID) extends ManagerEvent

	/** Notify a node of the given skeleton about an event */
	case class NotifyNode(uid: UID, nid: NodeId, serial: Int, event: NodeEvent) extends ManagerEvent
}
