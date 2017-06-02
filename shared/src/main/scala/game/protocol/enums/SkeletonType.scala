package game.protocol.enums

import game.UID
import game.skeleton.AbstractSkeleton
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}

sealed abstract class SkeletonType (val instantiate: UID => AbstractSkeleton)

object SkeletonType {
	case object Character extends SkeletonType(new CharacterSkeleton(_))
	case object Spell extends SkeletonType(new SpellSkeleton(_))
}
