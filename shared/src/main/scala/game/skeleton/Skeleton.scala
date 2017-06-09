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
	@pickle case object Character extends Skeleton(new CharacterSkeleton(_, _))
	@pickle case object DynamicArea extends Skeleton(new DynamicAreaSkeleton(_, _))
	@pickle case object KothStatus extends Skeleton(new KothStatusSkeleton(_, _))
	@pickle case object Point extends Skeleton(new PointSkeleton(_, _))
	@pickle case object Progress extends Skeleton(new ProgressSkeleton(_, _))
	@pickle case object Spell extends Skeleton(new SpellSkeleton(_, _))
}
