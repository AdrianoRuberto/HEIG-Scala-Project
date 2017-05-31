package game.protocol.enums

import game.UID
import game.skeleton.AbstractSkeleton
import game.skeleton.concrete.CharacterSkeleton

sealed abstract class SkeletonType (val instantiate: UID => AbstractSkeleton)

object SkeletonType {
	case object Character extends SkeletonType(new CharacterSkeleton(_))
}
