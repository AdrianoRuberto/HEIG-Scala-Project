package game

import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.timers.SetTimeoutHandle

object Lobby extends JSApp {
	private lazy val loader = dom.document.querySelector("#loader")

	private lazy val login = dom.document.querySelector("#login")
	private lazy val loginInput = dom.document.querySelector("#login input").asInstanceOf[html.Input]
	private lazy val loginTip = dom.document.querySelector("#login p")

	private lazy val lobby = dom.document.querySelector("#lobby")
	private lazy val lobbyName = dom.document.querySelector("#lobby #name")

	private var requestingUsername = false

	def main(): Unit = {
		dom.window.onload = (e: dom.Event) => init()
	}

	def init(): Unit = {
		loader.classList.add("fade-out")
		loader.addEventListener("transitionend", (e: dom.TransitionEvent) => {
			loader.parentNode.removeChild(loader)
			dom.window.sessionStorage.getItem("username") match {
				case null => requestUsername()
				case name => displayLobby(name)
			}
		})
	}

	def validateUsername(name: String): String = name.replaceAll("[^a-zA-Z0-9]", "").take(15)

	def requestUsername(): Unit = {
		requestingUsername = true
		login.classList.add("visible")
		loginInput.focus()

		loginInput.oninput = (_: dom.Event) => {
			loginInput.value = validateUsername(loginInput.value)
			triggerTipTimer()
		}

		loginInput.onkeyup = (e: dom.KeyboardEvent) => {
			if (e.keyCode == 13) {
				val name = validateUsername(loginInput.value)
				requestingUsername = false
				loginInput.blur()
				dom.window.sessionStorage.setItem("username", name)
				login.classList.add("fade-out")
				js.timers.setTimeout(500) {
					login.classList.remove("visible")
					login.classList.remove("fade-out")
					displayLobby(name)
				}
			}
		}

		loginInput.onblur = (_: dom.Event) => {
			if (requestingUsername) {
				loginInput.focus()
			}
		}
	}

	def displayLobby(name: String): Unit = {
		lobby.classList.add("visible")
		lobbyName.textContent = name
	}

	private var tipTimer: SetTimeoutHandle = _
	private def triggerTipTimer(): Unit = {
		js.timers.clearTimeout(tipTimer)
		loginTip.classList.remove("visible")
		tipTimer = js.timers.setTimeout(1500) {
			if (loginInput.value.nonEmpty) {
				loginTip.classList.add("visible")
			}
		}
	}
}
