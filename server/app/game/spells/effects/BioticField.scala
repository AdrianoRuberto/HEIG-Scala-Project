package game.spells.effects

import engine.geometry.Circle
import game.UID
import game.doodads.Doodad
import game.spells.effects.base.{SpellContext, SpellEffect, SpellEffectInstance}
import utils.Color

object BioticField extends SpellEffect {
	def instantiate(ctx: SpellContext) = new SpellEffectInstance(this, ctx) {
		override val duration: Double = 5000
		override val cooldown: Double = duration * 2

		/** Effect area */
		private val area = Circle(player.skeleton.position, 80)

		/** Trigger region */
		private val region = game.createRegion(area, enter, exit, filter = _ friendly initiator)
		private def enter(uid: UID): Unit = uid.skeleton.health.rate += 25
		private def exit(uid: UID): Unit = uid.skeleton.health.rate -= 25

		/** Visual doodad */
		private val visual = game.createDoodad(Doodad.Area.StaticArea(
			shape = area,
			fillColor = Color(255, 200, 127, 0.1),
			strokeColor = Color(255, 200, 127, 0.8)
		))

		override def end(): Unit = {
			region.remove()
			visual.remove()
		}
	}
}
