package game.doodads

import engine.entity.Entity
import game.UID
import game.client.Game
import game.doodads.Doodad._
import game.skeleton.AbstractSkeleton
import game.skeleton.core.CharacterSkeleton
import scala.language.implicitConversions

/**
  * This object is responsible to create the entity associated with the given
  * doodad constructor object.
  */
object DoodadFactory {
	def create(doodad: Doodad): Entity = doodad match {
		case Area.StaticArea(shape, fill, stroke, fillColor, strokeColor, strokeWidth) =>
			new area.StaticAreaEntity(shape, fill, stroke, fillColor, strokeColor, strokeWidth)
		case Area.DynamicArea(skeleton) => new area.DynamicAreaEntity(skeleton)
		case Area.Wall(shape, color) => new area.WallEntity(shape, color)

		case Hud.CtfStatus(skeleton) => new hud.CtfStatusEntity(skeleton)
		case Hud.KothStatus(skeleton) => new hud.KothStatusEntity(skeleton)
		case Hud.DeathScreen(duration) => new hud.DeathScreenEntity(duration)
		case Hud.Overtime(skeleton) => new hud.OvertimeEntity(skeleton)
		case Hud.VictoryScreen(msg, color) => new hud.VictoryScreenEntity(msg, color)

		case Spell.Sword(x, y, angle) => new spell.SwordEntity(x, y, angle)
		case Spell.Flag(location, holder, color) => new spell.FlagEntity(location, holder.map(resolveSkeleton[CharacterSkeleton]), color)
	}

	/** Implicitly resolves skeleton UIDs from the SkeletonManager in Game */
	private implicit def resolveSkeleton[T <: AbstractSkeleton](uid: UID): T = Game.getSkeleton(uid)
}
