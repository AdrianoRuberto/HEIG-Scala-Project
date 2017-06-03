package game.spells.effects

import engine.geometry.Vector
import game.UID
import game.server.BasicGame
import game.skeleton.concrete.SpellSkeleton

case class SpellContext (game: BasicGame, skeleton: SpellSkeleton, initiator: UID, point: Vector) {
	import game.UIDOps

	lazy val player: game.UIDOps = initiator
}
