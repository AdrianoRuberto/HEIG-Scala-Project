package engine

import engine.modules.{EngineEntities, EngineLoop, EngineScene}
import org.scalajs.dom
import org.scalajs.dom.html

final class Engine (val canvas: html.Canvas) extends EngineLoop with EngineScene with EngineEntities {
	private[engine] val ctx = canvas.getContext("2d").asInstanceOf[CanvasCtx]

	val camera = new Camera(this)
	val inputs = new Inputs(this)

	def setup(): Unit = {
		dom.document.addEventListener("mousemove", inputs.mouseHandler _)
		dom.document.addEventListener("mousedown", inputs.mouseHandler _)
		dom.document.addEventListener("mouseup", inputs.mouseHandler _)

		dom.document.addEventListener("keydown", inputs.keyboardHandler _)
		dom.document.addEventListener("keyup", inputs.keyboardHandler _)
	}
}

