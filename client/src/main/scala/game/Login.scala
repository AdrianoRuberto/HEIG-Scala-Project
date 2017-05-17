package game

import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle

object Login {
	private lazy val login = dom.document.querySelector("#login")
	private lazy val loginInput = dom.document.querySelector("#login input").asInstanceOf[html.Input]
	private lazy val loginTip = dom.document.querySelector("#login p")

	private var requestingUsername = false
	private var tipTimer: SetTimeoutHandle = _

	def setup(): Unit = {
		// Continuous username validation (+ tip display)
		loginInput.on(Event.Input) { _ =>
			loginInput.value = validateUsername(loginInput.value)
			triggerTipTimer()
		}

		// Handle [Enter] key press
		loginInput.on(Event.KeyUp) { e =>
			if (e.keyCode == 13) {
				val name = validateUsername(loginInput.value)
				if (name.nonEmpty) {
					requestingUsername = false
					loginInput.blur()
					dom.window.sessionStorage.setItem("username", name)
					login.classList.add("fade-out")
					js.timers.setTimeout(500) {
						login.classList.remove("visible")
						login.classList.remove("fade-out")
						Lobby.displayLobby(name)
					}
				}
			}
		}

		// Ensures that the focus stays on the input element
		loginInput.on(Event.Blur) { _ =>
			if (requestingUsername) {
				loginInput.focus()
			}
		}
	}

	/**
	  * Asks the user for their name.
	  */
	def requestUsername(): Unit = {
		requestingUsername = true
		login.classList.add("visible")
		loginInput.value = ""
		loginInput.focus()
		triggerTipTimer()
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
