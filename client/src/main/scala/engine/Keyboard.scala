package engine

import org.scalajs.dom
import scala.collection.mutable

class Keyboard private[engine] (engine: Engine) {
	var shift: Boolean = false
	var ctrl: Boolean = false
	var alt: Boolean = false

	private val states = mutable.Map.empty[String, Boolean]
	def key(name: String): Boolean = states.getOrElse(name, false)

	private[engine] def handler(event: dom.KeyboardEvent): Unit = if (!event.repeat) {
		shift = event.shiftKey
		ctrl = event.ctrlKey
		alt = event.altKey
		states.update(event.key, event.`type` == "keydown")
	}
}
