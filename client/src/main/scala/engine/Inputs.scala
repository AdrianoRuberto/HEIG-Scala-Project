package engine

import engine.Inputs.Monitor
import engine.entity.Entity
import engine.geometry.Vector2D
import org.scalajs.dom
import scala.scalajs.js

class Inputs private[engine] (engine: Engine) {
	private var shift: Boolean = false
	private var ctrl: Boolean = false
	private var alt: Boolean = false

	private var states = Map.empty[String, Boolean]

	private var handlers = Map[String, (Option[() => Unit], Option[() => Unit])](
		"alt-f" -> (
			Some(() => engine.drawBoundingBoxes = !engine.drawBoundingBoxes),
			None
		)
	)

	private var rawX: Double = 0
	private var rawY: Double = 0

	def mouseX: Double = (rawX + engine.camera.left + 0.5).floor
	def mouseY: Double = (rawY + engine.camera.top + 0.5).floor

	def mousePosition: Vector2D = Vector2D(mouseX, mouseY)

	object relative {
		def mouseX(implicit to: Entity): Double = {
			val box = to.boundingBox
			Inputs.this.mouseX - (box.left + box.width / 2 + 0.5).floor
		}

		def mouseY(implicit to: Entity): Double = {
			val box = to.boundingBox
			Inputs.this.mouseY - (box.top + box.height / 2 + 0.5).floor
		}

		def mousePosition(implicit to: Entity): Vector2D = Vector2D(mouseX, mouseY)
	}

	def key(name: String): Boolean = states.getOrElse(name, false)

	def down(name: String)(implicit monitor: Monitor): Boolean = monitorQuery(name, expected = true)
	def up(name: String)(implicit monitor: Monitor): Boolean = monitorQuery(name, expected = false)

	private def monitorQuery(name: String, expected: Boolean)(implicit monitor: Monitor): Boolean = {
		states.get(name) match {
			case None => false
			case Some(state) =>
				var (t, a, b) = monitor.states.getOrElse(name, Inputs.defaultMonitorState)
				if (t != engine.time) {
					t = engine.time
					a = b
					b = state
					monitor.states += (name -> (t, a, b))
				}
				b == expected && a != expected
		}
	}

	def registerKey(key: String)(cmd: => Unit): Unit = {
		handlers += (key.toLowerCase -> (Some(() => cmd), None))
	}

	def registerKey(key: String, down: () => Unit, up: () => Unit): Unit = {
		require(!key.contains("-"), "Cannot bind a KeyUp event for a key with a modifier")
		var state = false
		handlers += (key.toLowerCase -> (
			Some(() => if (!state) {state = true; down()}),
			Some(() => if (state) {state = false; up()})
		))
	}

	def unregisterKeys(keys: String*): Unit = for (key <- keys) handlers -= key

	private[engine] def mouseHandler(event: dom.MouseEvent): Unit = {
		val rect = engine.canvas.getClientRects()(0)
		rawX = event.clientX - rect.left
		rawY = event.clientY - rect.top

		if (event.`type` != "mousemove") {
			val state = event.`type` == "mousedown"
			val code = s"m${event.button+1}"
			if (engine.isRunning && (!engine.isLocked || !state) && states.getOrElse(code, false) != state) {
				states += (code -> state)
				if (attemptDispatch(code, state)) {
					event.preventDefault()
				}
			}
		}
	}

	private[engine] def keyboardHandler(event: dom.KeyboardEvent): Unit = if (!event.repeat) {
		val code = getKeyCode(event)
		shift = event.shiftKey
		ctrl = event.ctrlKey
		alt = event.altKey
		val state = event.`type` == "keydown"
		states += (code -> state)

		// Dispatch key press if engine is running and not locked
		if (engine.isRunning && (!engine.isLocked || !state)) {
			var codes = List(code)
			if (shift) codes = codes ::: codes.map("shift-" + _)
			if (ctrl) codes = codes ::: codes.map("ctrl-" + _)
			if (alt) codes = codes ::: codes.map("alt-" + _)
			if ((false /: codes) { case (a, c) => attemptDispatch(c, state) || a }) {
				event.preventDefault()
			}
		}
	}

	private def getKeyCode(event: dom.KeyboardEvent): String = {
		event.asInstanceOf[js.Dynamic].code.asInstanceOf[js.UndefOr[String]].map { code =>
			code.replaceFirst("^Digit|^Key|^Numpad|Left$|Right$", "")
		}.getOrElse {
			event.key match {
				case " " => "space"
				case key => key
			}
		}.toLowerCase
	}

	private[engine] def attemptDispatch(code: String, state: Boolean): Boolean = handlers.get(code) match {
		case Some((Some(handler), _)) if state => handler(); true
		case Some((_, Some(handler))) if !state => handler(); true
		case _ => false
	}
}

object Inputs {
	class Monitor {
		private[Inputs] var states: Map[String, (Double, Boolean, Boolean)] = Map.empty
	}
	private val defaultMonitorState = (0.0, false, false)
}
