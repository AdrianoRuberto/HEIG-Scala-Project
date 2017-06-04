package game.spells.effects

import game.spells.effects.SpellEffect.EffectInstance
import java.lang.Math._

object Sword extends SpellEffect {
	type Instance = SwordInstance
	def instantiate(ctx: SpellContext): SwordInstance = new SwordInstance(this, ctx)

	class SwordInstance(e: SpellEffect, c: SpellContext)
		extends EffectInstance(e: SpellEffect, c: SpellContext) {
		duration = 500
		cooldown = 1000

		override def gain(): Unit = {
			val p = player.skeleton.position
			val α = atan2(point.y - p.y, point.x - p.x)

			val targets = game.players.keys.filter { uid =>
				if (uid == initiator) false
				else {
					val q = uid.skeleton.position
					p distance q match {
						case d if d < 30 =>
							// Orientation does not matter if players are overlapping
							true
						case d if d < 80 =>
							// In range
							val β = atan2(q.y - p.y, q.x - p.x)
							val δ = abs(atan2(sin(β - α), cos(β - α)))
							δ <= Math.PI / 4 && (p distance q) <= 80
						case _ =>
							// Out of range
							false
					}
				}
			}

			for (target <- targets) {
				target.skeleton.health -= 30
			}
		}
	}
}
