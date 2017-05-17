package game

import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.JSApp

/**
  * Implementation of the lobby behavior.
  * Also the entry point of the application.
  */
object Lobby extends JSApp {
	// Loader
	private lazy val loader = dom.document.querySelector("#loader")

	// Lobby
	private lazy val lobby = dom.document.querySelector("#lobby")
	private lazy val lobbyName = dom.document.querySelector("#lobby #name")

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
				case null => Login.requestUsername()
				case name => displayLobby(name)
			}
		})

		lobbyName.addEventListener("click", (_: dom.MouseEvent) => {
			lobby.classList.add("fade-out")
			js.timers.setTimeout(500) {
				lobby.classList.remove("visible")
				lobby.classList.remove("fade-out")
				Login.requestUsername()
			}
		})

		Login.init()
	}

	/**
	  * Displays the lobby page.
	  *
	  * @param name the current user's name
	  */
	def displayLobby(name: String): Unit = {
		lobby.classList.add("visible")
		lobbyName.textContent = name
	}
}
