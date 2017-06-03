package game.protocol.enums

import game.UID
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}
import game.skeleton.{AbstractSkeleton, RemoteManager}

sealed abstract class SkeletonType[+S <: AbstractSkeleton] (ctor: (UID, Seq[RemoteManager]) => S) {
	def instantiate(uid: UID): S = ctor(uid, Seq.empty)
	def instantiate(remotes: Seq[RemoteManager]): S = ctor(UID.next, remotes)
}

object SkeletonType {
	case object Character extends SkeletonType(new CharacterSkeleton(_, _))
	case object Spell extends SkeletonType(new SpellSkeleton(_, _))
}
