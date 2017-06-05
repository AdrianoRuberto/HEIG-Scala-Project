package game.doodads

import engine.entity.Entity
import game.UID
import game.client.Game
import game.doodads.Doodad.{Area, Debug, Spell, Status}
import game.doodads.area.DynamicAreaEntity
import game.doodads.debug.PointEntity
import game.doodads.spell.SwordEntity
import game.doodads.status.KothStatusEntity
import game.skeleton.AbstractSkeleton
import scala.language.implicitConversions

object DoodadFactory {
	def create(doodad: Doodad): Entity = doodad match {
		case Area.DynamicArea(skeleton) => new DynamicAreaEntity(skeleton)
		case Debug.Point(skeleton) => new PointEntity(skeleton)
		case Status.Koth(skeleton) => new KothStatusEntity(skeleton)
		case Spell.Sword(x, y, angle) => new SwordEntity(x, y, angle)
	}

	private implicit def resolveSkeleton[T <: AbstractSkeleton](uid: UID): T = Game.getSkeleton(uid)
}
