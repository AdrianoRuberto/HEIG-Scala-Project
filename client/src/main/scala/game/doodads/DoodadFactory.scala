package game.doodads

import engine.entity.Entity
import game.UID
import game.client.Game
import game.doodads.Doodad.{Area, Debug, Hud, Spell}
import game.doodads.area.{DynamicAreaEntity, StaticAreaEntity}
import game.doodads.debug.PointEntity
import game.doodads.hud.{DeathScreenEntity, KothStatusEntity, VictoryScreenEntity}
import game.doodads.spell.SwordEntity
import game.skeleton.AbstractSkeleton
import scala.language.implicitConversions

object DoodadFactory {
	def create(doodad: Doodad): Entity = doodad match {
		case Area.StaticArea(shape, fill, stroke, fillColor, strokeColor, strokeWidth) =>
			new StaticAreaEntity(shape, fill, stroke, fillColor, strokeColor, strokeWidth)
		case Area.DynamicArea(skeleton) => new DynamicAreaEntity(skeleton)

		case Hud.KothStatus(skeleton) => new KothStatusEntity(skeleton)
		case Hud.DeathScreen(duration) => new DeathScreenEntity(duration)
		case Hud.VictoryScreen(msg, color, skeleton) => new VictoryScreenEntity(msg, color, skeleton)

		case Spell.Sword(x, y, angle) => new SwordEntity(x, y, angle)

		case Debug.Point(skeleton) => new PointEntity(skeleton)
	}

	private implicit def resolveSkeleton[T <: AbstractSkeleton](uid: UID): T = Game.getSkeleton(uid)
}
