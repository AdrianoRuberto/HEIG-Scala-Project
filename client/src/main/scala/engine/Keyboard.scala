package engine

import engine.Keyboard.Monitor
import org.scalajs.dom
import scala.scalajs.js

class Keyboard private[engine] (engine: Engine) {
	var shift: Boolean = false
	var ctrl: Boolean = false
	var alt: Boolean = false

	private var states = Map.empty[String, Boolean]

	private var handlers = Map[String, (Option[() => Unit], Option[() => Unit])](
		"alt-f" -> (
			Some(() => engine.drawBoundingBoxes = !engine.drawBoundingBoxes),
			None
		)
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
		handlers += (key.toLowerCase -> (Some(() => cmd), None))
	}

	def registerKey(key: String, down: () => Unit, up: () => Unit): Unit = {
		require(!key.contains("-"), "Cannot bind a KeyUp event for a key with a modifier")
		handlers += (key.toLowerCase -> (Some(down), Some(up)))
	}

	def unregisterKeys(keys: String*): Unit = for (key <- keys ) handlers -= key

	private[engine] def handler(event: dom.KeyboardEvent): Unit = if (!event.repeat) {
		val code = getKeyCode(event)
		shift = event.shiftKey
		ctrl = event.ctrlKey
		alt = event.altKey
		val state = event.`type` == "keydown"
		states += (code -> state)

		// Dispatch key press if engine is running and not locked
		if (engine.isRunning && !engine.isLocked) {
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

	private def attemptDispatch(code: String, state: Boolean): Boolean = handlers.get(code) match {
		case Some((Some(handler), _)) if state => handler(); true
		case Some((_, Some(handler))) if !state => handler(); true
		case _ => false
	}
}

object Keyboard {
	class Monitor {
		private[Keyboard] var states: Map[String, (Double, Boolean, Boolean)] = Map.empty
	}
	private val defaultMonitorState = (0.0, false, false)
}
