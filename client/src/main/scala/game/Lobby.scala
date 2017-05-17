package game

import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.timers.SetTimeoutHandle

/**
  * Implementation of the lobby behavior.
  * Also the entry point of the application.
  */
object Lobby extends JSApp {
	// Loader
	private lazy val loader = dom.document.querySelector("#loader")

	// Login
	private lazy val login = dom.document.querySelector("#login")
	private lazy val loginInput = dom.document.querySelector("#login input").asInstanceOf[html.Input]
	private lazy val loginTip = dom.document.querySelector("#login p")

	// Lobby
	private lazy val lobby = dom.document.querySelector("#lobby")
	private lazy val lobbyName = dom.document.querySelector("#lobby #name")

	private var requestingUsername = false
	private var tipTimer: SetTimeoutHandle = _

	/**
	  * Application entry point, waits for the page to be fully loaded and
	  * then calls [[init]].
	  */
	def main(): Unit = {
		dom.window.onload = (e: dom.Event) => init()
	}

	/**
	  * Called once the application is fully loaded, removes the loaded and
	  * then either display the lobby or ask the user for their name.
	  */
	private def init(): Unit = {
		loader.classList.add("fade-out")
		loader.addEventListener("transitionend", (e: dom.TransitionEvent) => {
			loader.parentNode.removeChild(loader)
			dom.window.sessionStorage.getItem("username") match {
				case null => requestUsername()
				case name => displayLobby(name)
			}
		})
	}

	/**
	  * Ensures that the given username is a valid one.
	  * A legal username is formed from no more than 15 letters and digits.
	  *
	  * @param name the username to validate
	  * @return the username with illegal characters removed
	  */
	private def validateUsername(name: String): String = name.replaceAll("[^a-zA-Z0-9]", "").take(15)

	/**
	  * Asks the user for their name.
	  */
	private def requestUsername(): Unit = {
		requestingUsername = true
		login.classList.add("visible")
		loginInput.focus()

		// Continuous username validation (+ tip display)
		loginInput.oninput = (_: dom.Event) => {
			loginInput.value = validateUsername(loginInput.value)
			triggerTipTimer()
		}

		// Handle [Enter] key press
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

		// Ensures that the focus stays on the input element
		loginInput.onblur = (_: dom.Event) => {
			if (requestingUsername) {
				loginInput.focus()
			}
		}
	}

	/**
	  * Displays the lobby page.
	  *
	  * @param name the current user's name
	  */
	private def displayLobby(name: String): Unit = {
		lobby.classList.add("visible")
		lobbyName.textContent = name
	}

	/**
	  * Schedules the login tip to be displayed in 1.5 sec.
	  * If this function is called again before the timer expires, the timer is reset.
	  * The tip will only be displayed if the username input is not empty.
	  */
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
