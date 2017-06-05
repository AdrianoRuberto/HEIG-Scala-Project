package game.doodads

import engine.entity.Entity
import game.doodads.Doodad.Spell
import game.doodads.spell.SwordEntity

object DoodadFactory {
	def create(doodad: Doodad): Entity = doodad match {
		case Spell.Sword(x, y, angle) => new SwordEntity(x, y, angle)
	}
}
