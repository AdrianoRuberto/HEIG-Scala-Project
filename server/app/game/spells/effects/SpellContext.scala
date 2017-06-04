package game.spells.effects

import engine.geometry.Vector2D
import game.UID
import game.server.BasicGame
import game.skeleton.concrete.SpellSkeleton

case class SpellContext (game: BasicGame, skeleton: SpellSkeleton, initiator: UID, point: Vector2D) {
	import game.UIDOps

	lazy val player: game.UIDOps = initiator
}
