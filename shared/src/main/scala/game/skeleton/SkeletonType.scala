package game.skeleton

import game.UID
import game.skeleton.concrete._

sealed abstract class SkeletonType[+S <: AbstractSkeleton] (ctor: (UID, Seq[RemoteManager]) => S) {
	def instantiate(uid: UID): S = ctor(uid, Seq.empty)
	def instantiate(remotes: Seq[RemoteManager]): S = ctor(UID.next, remotes)
}

object SkeletonType {
	case object Character extends SkeletonType(new CharacterSkeleton(_, _))
	case object DynamicArea extends SkeletonType(new DynamicAreaSkeleton(_, _))
	case object KothStatus extends SkeletonType(new KothStatusSkeleton(_, _))
	case object Point extends SkeletonType(new PointSkeleton(_, _))
	case object Spell extends SkeletonType(new SpellSkeleton(_, _))
}
