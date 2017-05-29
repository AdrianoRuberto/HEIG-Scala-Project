package game.skeleton

import game.UID

class Closet {
	private var skeletons: Map[UID, AbstractSkeleton] = Map.empty

	def get(uid: UID): AbstractSkeleton = skeletons.get(uid) match {
		case Some(skeleton) => skeleton
		case None => throw new NoSuchElementException(s"No skeleton defined for UID: $uid")
	}

	def getAs[T <: AbstractSkeleton](uid: UID): T = get(uid).asInstanceOf[T]

	def handleEvent(event: Event.ClosetEvent): Unit = event match {
		case Event.InstantiateSkeleton(tpe, uid) =>
			val skeleton = tpe.instantiate(uid)
			skeletons += (uid -> skeleton)
		case Event.NotifySkeleton(uid, ev) =>
			get(uid).receive(ev)
	}

	def clear(): Unit = skeletons = Map.empty
}
