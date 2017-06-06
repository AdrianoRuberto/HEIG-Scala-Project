package game.skeleton

import game.UID
import game.skeleton.concrete._

sealed abstract class Skeleton[+S <: AbstractSkeleton] (ctor: (UID, Seq[RemoteManagerAgent]) => S) {
	def instantiate(uid: UID): S = ctor(uid, Seq.empty)
	def instantiate(remotes: Seq[RemoteManagerAgent]): S = ctor(UID.next, remotes)
}

object Skeleton {
	case object Character extends Skeleton(new CharacterSkeleton(_, _))
	case object DynamicArea extends Skeleton(new DynamicAreaSkeleton(_, _))
	case object KothStatus extends Skeleton(new KothStatusSkeleton(_, _))
	case object Point extends Skeleton(new PointSkeleton(_, _))
	case object Progress extends Skeleton(new ProgressSkeleton(_, _))
	case object Spell extends Skeleton(new SpellSkeleton(_, _))
}
