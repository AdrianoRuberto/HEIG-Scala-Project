package game.skeleton

import game.UID
import game.skeleton.node.NodeEvent
import macros.pickle

@pickle sealed trait ManagerEvent

object ManagerEvent {
	/** Instantiates a new skeleton of the given type with a given UID */
	@pickle case class InstantiateSkeleton(tpe: Skeleton[_ <: AbstractSkeleton], uid: UID) extends ManagerEvent
	@pickle case class CollectSkeleton(uid: UID) extends ManagerEvent

	/** Notify a node of the given skeleton about an event */
	@pickle case class NotifyNode(uid: UID, nid: NodeId, serial: Int, event: NodeEvent) extends ManagerEvent
}
