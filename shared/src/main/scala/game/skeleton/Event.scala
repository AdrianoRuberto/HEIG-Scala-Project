package game.skeleton

import boopickle.Default._
import game.UID

object Event {
	sealed trait SkeletonEvent
	case class NodeUpdate(nid: Int, value: Array[Byte]) extends SkeletonEvent

	sealed trait ClosetEvent
	case class InstantiateSkeleton(tpe: Type, uid: UID) extends ClosetEvent
	case class NotifySkeleton(uid: UID, event: SkeletonEvent) extends ClosetEvent

	private implicit val UIDPickler = UID.pickler
	implicit val skeletonEventPickler: Pickler[SkeletonEvent] = generatePickler[SkeletonEvent]
	implicit val closetEventPickler: Pickler[ClosetEvent] = generatePickler[ClosetEvent]
}
