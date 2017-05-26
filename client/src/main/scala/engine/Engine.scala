package engine

import engine.modules.{EngineEntities, EngineLoop, EngineScene}
import org.scalajs.dom
import org.scalajs.dom.html

final class Engine (val canvas: html.Canvas) extends EngineLoop with EngineScene with EngineEntities {
	private[engine] val ctx = canvas.getContext("2d").asInstanceOf[CanvasCtx]

	val camera = new Camera(this)
	val mouse = new Mouse(this)
	val keyboard = new Keyboard(this)

	def setup(): Unit = {
		dom.document.addEventListener("mousemove", mouse.handler _)
		dom.document.addEventListener("mousedown", mouse.handler _)
		dom.document.addEventListener("mouseup", mouse.handler _)

		dom.document.addEventListener("keydown", keyboard.handler _)
		dom.document.addEventListener("keyup", keyboard.handler _)
	}
}

