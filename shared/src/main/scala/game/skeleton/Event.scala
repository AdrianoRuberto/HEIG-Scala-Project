package game.skeleton

import boopickle.Default._
import game.UID

/**
  * Skeleton-related events
  */
object Event {
	/** Events targeting node instances */
	sealed trait NodeEvent

	/** Events targeting [[SimpleNode]] instances */
	sealed trait SimpleNodeEvent extends NodeEvent
	/** Update to a [[SimpleNode]] value */
	case class SimpleUpdate(value: Array[Byte]) extends SimpleNodeEvent

	sealed trait InterpolatedNodeEvent extends NodeEvent
	case class InterpolatedUpdate(target: Double, duration: Double) extends InterpolatedNodeEvent

	/** Events targeting a [[SkeletonManager]] */
	sealed trait ManagerEvent
	/** Instantiates a new skeleton of the given type with a given UID */
	case class InstantiateSkeleton(tpe: Type, uid: UID) extends ManagerEvent
	/** Notify a node of the given skeleton about an event */
	case class NotifyNode(uid: UID, nid: NodeId, event: NodeEvent) extends ManagerEvent

	private implicit val UIDPickler = UID.pickler
	private implicit val NodeIdPickler = NodeId.pickler

	implicit val skeletonEventPickler: Pickler[NodeEvent] = generatePickler[NodeEvent]
	implicit val closetEventPickler: Pickler[ManagerEvent] = generatePickler[ManagerEvent]
}
