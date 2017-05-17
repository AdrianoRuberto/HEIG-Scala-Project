package game

import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.JSApp

/**
  * Implementation of the lobby behavior.
  * Also the entry point of the application.
  */
object Lobby extends JSApp {
	private lazy val loader = dom.document.querySelector("#loader")

	private lazy val lobby = dom.document.querySelector("#lobby")
	private lazy val lobbyName = dom.document.querySelector("#lobby #name")
	private lazy val lobbyButton = dom.document.querySelector("#lobby button").asInstanceOf[html.Button]

	private var searching = false

	private final val SearchForGame = "Search for game"
	private final val CancelSearch = "Cancel search"

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

		loader.on(Event.TransitionEnd) { _ =>
			loader.parentNode.removeChild(loader)
			dom.window.sessionStorage.getItem("username") match {
				case null => Login.requestUsername()
				case name => displayLobby(name)
			}
		}

		lobbyName.on(Event.Click) { _ =>
			lobby.classList.add("fade-out")
			js.timers.setTimeout(500) {
				lobby.classList.remove("visible")
				lobby.classList.remove("fade-out")
				Login.requestUsername()
			}
		}

		lobbyButton.on(Event.Click) { _ =>
			if (searching) {
				lobbyButton.textContent = SearchForGame
				searching = false
			} else {
				lobbyButton.textContent = CancelSearch
				searching = true
			}
		}

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
		lobbyButton.textContent = SearchForGame
	}
}
