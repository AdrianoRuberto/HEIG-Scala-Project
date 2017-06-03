package game.skeleton

import game.UID

class SkeletonManager {
	private var skeletons: Map[UID, AbstractSkeleton] = Map.empty

	def get(uid: UID): AbstractSkeleton = skeletons.get(uid) match {
		case Some(skeleton) => skeleton
		case None => throw new NoSuchElementException(s"No skeleton defined for UID: $uid")
	}

	def getAs[T <: AbstractSkeleton](uid: UID): T = get(uid).asInstanceOf[T]

	def receive(event: ManagerEvent): Unit = event match {
		case ManagerEvent.InstantiateSkeleton(tpe, uid) =>
			val skeleton = tpe.instantiate(uid)
			skeletons += (uid -> skeleton)
		case ManagerEvent.CollectSkeleton(uid) =>
			skeletons -= uid
		case n @ ManagerEvent.NotifyNode(uid, _, _, _) =>
			get(uid).receive(n)
	}

	def clear(): Unit = skeletons = Map.empty
}
