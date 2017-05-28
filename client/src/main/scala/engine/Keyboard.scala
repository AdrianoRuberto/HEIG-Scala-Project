package engine

import engine.Keyboard.Monitor
import org.scalajs.dom

class Keyboard private[engine] (engine: Engine) {
	var shift: Boolean = false
	var ctrl: Boolean = false
	var alt: Boolean = false

	private var states = Map.empty[String, Boolean]

	private var handlers = Map[String, () => Unit](
		"ctrl-f" -> (() => engine.drawBoundingBoxes = !engine.drawBoundingBoxes)
	)

	def key(name: String): Boolean = states.getOrElse(name, false)

	def down(name: String)(implicit monitor: Monitor): Boolean = monitorQuery(name, expected = true)
	def up(name: String)(implicit monitor: Monitor): Boolean = monitorQuery(name, expected = false)

	private def monitorQuery(name: String, expected: Boolean)(implicit monitor: Monitor): Boolean = {
		states.get(name) match {
			case None => false
			case Some(state) =>
				var (t, a, b) = monitor.states.getOrElse(name, Keyboard.defaultMonitorState)
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
		handlers += (key.toLowerCase -> (() => cmd))
	}

	private[engine] def handler(event: dom.KeyboardEvent): Unit = if (!event.repeat) {
		shift = event.shiftKey
		ctrl = event.ctrlKey
		alt = event.altKey
		val state = event.`type` == "keydown"
		states += (event.key -> state)

		// Dispatch key press if engine is running and not locked
		if (state && engine.isRunning && !engine.isLocked) {
			var key = event.key
			if (shift) key = "shift-" + key
			if (alt) key = "alt-" + key
			if (ctrl) key = "ctrl-" + key
			handlers.get(key.toLowerCase) match {
				case Some(handler) => handler()
				case None => return
			}
			event.preventDefault()
		}
	}
}

object Keyboard {
	class Monitor {
		private[Keyboard] var states: Map[String, (Double, Boolean, Boolean)] = Map.empty
	}
	private val defaultMonitorState = (0.0, false, false)
}
