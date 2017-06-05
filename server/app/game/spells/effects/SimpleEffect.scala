package game.spells.effects

private[effects] trait SimpleEffect extends SpellEffect { parent =>
	val duration: Double = 0.0
	val cooldown: Double = 0.0

	def apply(ctx: SpellContext): Unit = ()
	def tick(ctx: SpellContext, dt: Double): Unit = ()
	def end(ctx: SpellContext): Unit = ()

	final def instantiate(ctx: SpellContext) = new SpellEffectInstance(this, ctx) {
		override val duration: Double = parent.duration
		override val cooldown: Double = parent.cooldown
		parent.apply(ctx)
		override def tick(dt: Double): Unit = parent.tick(ctx, dt)
		override def end(): Unit = parent.end(ctx)
	}
}
