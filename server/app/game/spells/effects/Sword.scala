package game.spells.effects

import game.doodads.Doodad
import java.lang.Math._

object Sword extends SpellEffect {
	def instantiate(ctx: SpellContext) = new SpellEffectInstance(this, ctx) {
		override val duration: Double = 500
		override val cooldown: Double = 1000

		private val p = player.skeleton.position
		private val α = atan2(point.y - p.y, point.x - p.x)

		player.skeleton.facingDirection.value = α
		player.skeleton.facingOverride.value = true
		private val swordDoodad = ctx.game.createGlobalDoodad(Doodad.Spell.Sword(p.x, p.y, α))

		private val targets = game.players.filter(_ hostile initiator).filter { uid =>
			val q = uid.skeleton.position
			p <-> q match {
				case d if d < 25 =>
					// Orientation does not matter if players are overlapping
					true
				case d if d < 90 =>
					// In range, check orientation
					val β = atan2(q.y - p.y, q.x - p.x)
					abs(atan2(sin(β - α), cos(β - α))) <= 3 * Math.PI / 8
				case _ =>
					// Out of range
					false
			}
		}

		for (target <- targets) {
			target.skeleton.health -= 30
		}

		override def end(): Unit = {
			game.destroyDoodad(swordDoodad)
			player.skeleton.facingOverride.value = false
		}
	}
}
