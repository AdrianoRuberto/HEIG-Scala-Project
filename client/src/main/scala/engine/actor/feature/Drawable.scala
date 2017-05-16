package engine
package actor
package feature

import engine.utils.Layer

trait Drawable extends Actor with Positioned {
	private[engine] override def registerWith(engine: Engine): Unit = {
		super.registerWith(engine)
		engine.drawables += this
	}

	private[engine] override def unregisterFrom(engine: Engine): Unit = {
		engine.drawables -= this
		super.unregisterFrom(engine)
	}

	def layer: Layer
	def draw(ctx: CanvasCtx): Unit
}

object Drawable {
	implicit val drawableOrdering: Ordering[Drawable] = Ordering.by(_.layer.strata)
}
