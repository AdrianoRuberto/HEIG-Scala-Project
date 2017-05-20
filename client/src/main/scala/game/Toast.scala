package game

import org.scalajs.dom
import scala.scalajs.js

object Toast {
	private lazy val toast = dom.document.querySelector("#toast")

	def show(text: String): Unit = {
		toast.textContent = text
		toast.classList.add("visible")
		js.timers.setTimeout(5000) {
			toast.classList.add("fade-out")
			js.timers.setTimeout(1000) {
				toast.classList.remove("visible")
				toast.classList.remove("fade-out")
			}
		}
	}
}
