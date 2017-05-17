package game

import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js

/**
  * Implementation of the lobby behavior.
  * Also the entry point of the application.
  */
object Lobby {
	private lazy val lobby = dom.document.querySelector("#lobby")
	private lazy val lobbyName = dom.document.querySelector("#lobby #name")
	private lazy val lobbyButton = dom.document.querySelector("#lobby button").asInstanceOf[html.Button]

	private var player: Player = _
	private var searching = false

	private final val SearchForGame = "Search for game"
	private final val CancelSearch = "Cancel search"

	/**
	  * Called once the application is fully loaded, removes the loaded and
	  * then either display the lobby or ask the user for their name.
	  */
	def setup(): Unit = {
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
				Server.searchGame(player)
			}
		}
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
		player = Player(name)
	}

	def message(lm: ServerMessage.LobbyMessage): Unit = lm match {
		case ServerMessage.QueueUpdate(length) => println("queue length is", length)
		case ServerMessage.GameFound(teams) => println("game found", teams)
	}
}
