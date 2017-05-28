package game.client

import org.scalajs.dom

object Toast {
	private lazy val toast = dom.document.querySelector("#toast")

	def show(text: String): Unit = {
		toast.textContent = text
		toast.classList.add("visible")
		App.timeout(5000) {
			toast.classList.add("fade-out")
			App.timeout(1000) {
				toast.classList.remove("visible")
				toast.classList.remove("fade-out")
			}
		}
	}
}
