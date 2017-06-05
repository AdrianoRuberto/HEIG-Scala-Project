package game.spells.effects

import engine.geometry.Circle
import game.UID
import game.doodads.Doodad
import game.server.Region
import game.skeleton.SkeletonType
import game.skeleton.concrete.DynamicAreaSkeleton
import game.spells.effects.SpellEffect.EffectInstance

object BioticField extends SpellEffect {
	type Instance = BioticFieldInstance
	def instantiate(ctx: SpellContext): BioticFieldInstance = new BioticFieldInstance(this, ctx)

	class BioticFieldInstance(e: SpellEffect, c: SpellContext)
		extends EffectInstance(e: SpellEffect, c: SpellContext) {

		val effectArea = Circle(player.skeleton.x.current, player.skeleton.y.current, 50)

		def enter(uid: UID): Unit = {
			uid.skeleton.health.rate += 40
		}

		def exit (uid: UID): Unit = {
			uid.skeleton.health.rate -= 40
		}

		val visualSkeleton: DynamicAreaSkeleton = game.createGlobalSkeleton(SkeletonType.DynamicArea)
		visualSkeleton.shape.value = effectArea
		visualSkeleton.strokeWidth.value = 2

		val visual: UID = game.createGlobalDoodad(Doodad.Area.DynamicArea(visualSkeleton.uid))

		duration = 5000
		cooldown = duration * 2

		val region: Region = game.createRegion(effectArea, enter, exit)

		override def lose(): Unit = {
			region.remove()
			visualSkeleton.collect()
			game.destroyDoodad(visual)
		}
	}
}
