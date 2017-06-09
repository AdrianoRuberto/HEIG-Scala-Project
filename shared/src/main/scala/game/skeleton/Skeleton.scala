package game.skeleton

import game.UID
import game.doodads.area.DynamicAreaSkeleton
import game.doodads.hud.KothStatusSkeleton
import game.skeleton.concrete._
import macros.pickle

@pickle sealed abstract class Skeleton[+S <: AbstractSkeleton] (ctor: (UID, Iterable[RemoteManagerAgent]) => S) {
	def instantiate(uid: UID): S = ctor(uid, Seq.empty)
	def instantiate(remotes: Iterable[RemoteManagerAgent]): S = ctor(UID.next, remotes)
}

object Skeleton {
	@pickle case object Character extends Skeleton(CharacterSkeleton)
	@pickle case object DynamicArea extends Skeleton(DynamicAreaSkeleton)
	@pickle case object KothStatus extends Skeleton(KothStatusSkeleton)
	@pickle case object Spell extends Skeleton(SpellSkeleton)
}
