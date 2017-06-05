package game.spells.effects

object Flagellation extends SpellEffect with SimpleEffect {
	override def apply(ctx: SpellContext): Unit = {
		ctx.player.skeleton.health.consume(100)
	}
}
