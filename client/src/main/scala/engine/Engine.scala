package engine

import engine.actor.{Actor, feature}
import engine.utils.Point
import org.scalajs.dom
import scala.collection.mutable

class Engine(val canvas: Canvas) {
	private[engine] val updatables = mutable.Set.empty[Actor with feature.Updatable]
	private[engine] val drawables = mutable.SortedSet.empty[Actor with feature.Drawable]
	private[engine] val mouseEnabled = mutable.Set.empty[Actor with feature.MouseEvents]
	private[engine] val keyboardEnabled = mutable.Set.empty[Actor with feature.KeyboardEvents]

	def registerActor(actor: Actor): Unit = actor.registerWith(this)
	def unregisterActor(actor: Actor): Unit = actor.unregisterFrom(this)

	def mouseHandler(event: dom.MouseEvent): Unit = if (mouseEnabled.nonEmpty) {
		val rect = canvas.element.getClientRects().apply(0)
		val x = event.clientX - rect.left
		val y = event.clientY - rect.top
		for (actor <- mouseEnabled) {
			actor.handleMouse(event.`type`, x, y, event.buttons)
		}
	}

	private var lastTimestamp: Double = Double.NaN

	def loop(timestamp: Double): Unit = {
		// Compute delta time
		val dt = if (lastTimestamp.isNaN) 0 else timestamp - lastTimestamp
		lastTimestamp = timestamp

		// Update all actors
		for (actor <- updatables) {
			actor.update(dt)
		}

		// Draw actors
		val ctx = canvas.ctx
		ctx.setTransform(1, 0, 0, 1, 0, 0)
		ctx.clearRect(0, 0, canvas.width, canvas.height)
		for (actor <- drawables; Point(x, y) = actor.position) {
			ctx.save()
			ctx.translate(x, y)
			actor.draw(ctx)
			ctx.restore()
		}

		// Request a new animation frame
		dom.window.requestAnimationFrame(loop _)
	}

	def start(): Unit = {
		dom.document.addEventListener("mousedown", mouseHandler _)
		dom.document.addEventListener("mouseup", mouseHandler _)
		dom.document.addEventListener("mousemove", mouseHandler _)
		dom.document.addEventListener("click", mouseHandler _)
		dom.window.requestAnimationFrame(loop _)
	}
}

