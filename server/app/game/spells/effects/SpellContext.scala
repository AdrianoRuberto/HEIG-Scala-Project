package game.spells.effects

import game.UID
import game.server.BasicGame
import game.skeleton.concrete.{CharacterSkeleton, SpellSkeleton}

case class SpellContext (game: BasicGame, skeleton: SpellSkeleton, initiator: UID) {
	import game.UIDOps
	lazy val player: game.UIDOps = initiator
	lazy val playerSkeleton: CharacterSkeleton = player.skeleton
}
