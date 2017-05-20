package engine

import engine.entity.{Entity, feature}
import engine.utils.Point
import org.scalajs.dom
import org.scalajs.dom.html
import scala.collection.mutable

class Engine(val canvas: html.Canvas) {
	private val ctx = canvas.getContext("2d").asInstanceOf[CanvasCtx]

	private var running = false
	private var locked = true

	def isRunning: Boolean = running
	def isLocked: Boolean = locked

	private[engine] val entityIdsAllocator = new Entity.IdAllocator
	private[engine] val updatables = mutable.Set.empty[Entity with feature.Updatable]
	private[engine] val drawables = mutable.SortedSet.empty[Entity with feature.Drawable]
	private[engine] val mouseEnabled = mutable.Set.empty[Entity with feature.MouseEvents]
	private[engine] val keyboardEnabled = mutable.Set.empty[Entity with feature.KeyboardEvents]

	private var lastUpdateTimestamp: Double = Double.NaN

	def setup(): Unit = {
		dom.document.addEventListener("mousedown", mouseHandler _)
		dom.document.addEventListener("mouseup", mouseHandler _)
		dom.document.addEventListener("mousemove", mouseHandler _)
		dom.document.addEventListener("click", mouseHandler _)
	}

	def registerActor(actor: Entity): Unit = {
		actor.registerWith(this)
	}
	def unregisterActor(actor: Entity): Unit = {
		actor.unregisterFrom(this)
	}

	def mouseHandler(event: dom.MouseEvent): Unit = if (!locked && mouseEnabled.nonEmpty) {
		val rect = canvas.getClientRects().apply(0)
		val x = event.clientX - rect.left
		val y = event.clientY - rect.top
		for (actor <- mouseEnabled) {
			actor.handleMouse(event.`type`, x, y, event.buttons)
		}
	}

	def loop(timestamp: Double): Unit = if (running) {
		// Compute delta time
		val dt = if (lastUpdateTimestamp.isNaN) 0 else timestamp - lastUpdateTimestamp
		lastUpdateTimestamp = timestamp

		// Update all actors
		for (actor <- updatables) {
			actor.update(dt)
		}

		// Draw actors
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
		running = true
		lastUpdateTimestamp = Double.NaN
		dom.window.requestAnimationFrame(loop _)
	}

	def lock(): Unit = locked = true
	def unlock(): Unit = locked = false

	def stop(): Unit = {
		running = false
		ctx.clearRect(0, 0, canvas.width, canvas.height)
	}
}

