package ctf

import ctf.actors.Actor
import ctf.utils.Point
import org.scalajs.dom
import scala.collection.mutable

class Engine(val canvas: Canvas) {
	private val actorsSet = mutable.SortedSet[Actor]()
	def actors: Iterator[Actor] = actorsSet.iterator

	def registerActor(actor: Actor): Unit = actorsSet += actor

	def mouseHandler(event: dom.MouseEvent): Unit = {
		val rect = canvas.element.getClientRects().apply(0)
		val x = event.clientX - rect.left
		val y = event.clientY - rect.top
		for (actor <- actors) actor.handleMouse(event.`type`, x, y, event.buttons)
	}

	private var lastTimestamp: Double = Double.NaN

	def update(timestamp: Double): Unit = {
		// Compute delta time
		val dt = if (lastTimestamp.isNaN) timestamp else timestamp - lastTimestamp
		lastTimestamp = timestamp

		// Update all actors
		for (actor <- actors) actor.update(dt)

		// Draw actors
		val ctx = canvas.ctx
		ctx.setTransform(1, 0, 0, 1, 0, 0)
		ctx.clearRect(0, 0, canvas.width, canvas.height)
		for (actor <- actors; Point(x, y) = actor.position) {
			ctx.save()
			ctx.translate(x, y)
			actor.draw(ctx)
			ctx.restore()
		}

		// Request a new animation frame
		dom.window.requestAnimationFrame(update _)
	}

	def start(): Unit = {
		dom.document.addEventListener("mousedown", mouseHandler _)
		dom.document.addEventListener("mouseup", mouseHandler _)
		dom.document.addEventListener("mousemove", mouseHandler _)
		dom.document.addEventListener("click", mouseHandler _)
		dom.window.requestAnimationFrame(update _)
	}
}

