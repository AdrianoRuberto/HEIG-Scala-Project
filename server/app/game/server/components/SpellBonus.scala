package game.server.components

import engine.geometry.Vector2D
import game.spells.Spell

case class SpellBonus (spell: Spell, location: Vector2D, cooldown: Double, radius: Double) {

}
