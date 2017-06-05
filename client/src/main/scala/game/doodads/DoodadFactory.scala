package game.doodads

import engine.entity.Entity
import game.UID
import game.client.Game
import game.doodads.Doodad.{Debug, Spell}
import game.doodads.debug.PointEntity
import game.doodads.spell.SwordEntity
import game.skeleton.AbstractSkeleton
import scala.language.implicitConversions

object DoodadFactory {
	def create(doodad: Doodad): Entity = doodad match {
		case Debug.Point(skeleton) => new PointEntity(skeleton)
		case Spell.Sword(x, y, angle) => new SwordEntity(x, y, angle)
	}

	private implicit def resolveSkeleton[T <: AbstractSkeleton](uid: UID): T = Game.getSkeleton(uid)
}
