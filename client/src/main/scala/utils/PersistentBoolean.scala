package utils

import org.scalajs.dom
import scala.language.implicitConversions

class PersistentBoolean private (name: String, default: Boolean) {
	private var state: Boolean = dom.window.sessionStorage.getItem(name) == "1"
	if (dom.window.sessionStorage.getItem(name) == null) value = default

	def value: Boolean = state

	def value_= (value: Boolean): Unit = {
		state = value
		dom.window.sessionStorage.setItem(name, if (value) "1" else "0")
	}

	def toggle(): Unit = value = !value
}

object PersistentBoolean {
	def apply(name: String, default: Boolean): PersistentBoolean = new PersistentBoolean(name, default)
	@inline implicit def toBoolean(pb: PersistentBoolean): Boolean = pb.value
}
