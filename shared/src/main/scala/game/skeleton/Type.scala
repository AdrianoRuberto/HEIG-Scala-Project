package game.skeleton

import game.UID
import game.skeleton.concrete.CharacterSkeleton

sealed abstract class Type (val instantiate: UID => AbstractSkeleton)

object Type {
	case object Character extends Type(new CharacterSkeleton(_))
}
