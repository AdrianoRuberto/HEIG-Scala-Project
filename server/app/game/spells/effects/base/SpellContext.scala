package game.spells.effects.base

import engine.geometry.Vector2D
import game.UID
import game.server.BasicGame
import game.skeleton.core.SpellSkeleton

case class SpellContext (game: BasicGame, skeleton: SpellSkeleton, initiator: UID, point: Vector2D) {
	lazy val player: game.UIDOps = new game.UIDOps(initiator)
}
