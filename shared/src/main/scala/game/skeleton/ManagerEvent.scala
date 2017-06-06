package game.skeleton

import game.UID
import game.skeleton.node.NodeEvent

sealed trait ManagerEvent

object ManagerEvent {
	Skeleton.Character

	/** Instantiates a new skeleton of the given type with a given UID */
	case class InstantiateSkeleton(tpe: Skeleton[_ <: AbstractSkeleton], uid: UID) extends ManagerEvent
	case class CollectSkeleton(uid: UID) extends ManagerEvent

	/** Notify a node of the given skeleton about an event */
	case class NotifyNode(uid: UID, nid: NodeId, serial: Int, event: NodeEvent) extends ManagerEvent
}
