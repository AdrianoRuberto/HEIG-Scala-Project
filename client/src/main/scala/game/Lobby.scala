package game

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.timers.SetIntervalHandle

/**
  * Implementation of the lobby behavior.
  */
object Lobby {
	private lazy val lobby = dom.document.querySelector("#lobby")
	private lazy val playerName = dom.document.querySelector("#lobby #name")
	private lazy val button = dom.document.querySelector("#lobby button").asInstanceOf[html.Button]
	private lazy val dots = dom.document.querySelector("#lobby #dots")
	private lazy val stats = dom.document.querySelector("#lobby #stats")

	private var searching = false
	private var found = false

	private var statsInterval: SetIntervalHandle = null
	private var searchStart: Double = 0.0
	private var playersInQueue: Int = 0

	private final val SearchForGame = "Search for game"
	private final val CancelSearch = "Cancel search"

	/** Setups lobby events handlers */
	def setup(): Unit = {
		playerName.on(Event.Click) { _ =>
			if (!found) {
				if (searching) stopSearch()
				lobby.classList.add("fade-out")
				js.timers.setTimeout(500) {
					lobby.classList.remove("visible")
					lobby.classList.remove("fade-out")
					Login.requestUsername()
				}
			}
		}

		button.on(Event.Click) { e =>
			if (!found) {
				if (searching) stopSearch()
				else startSearch(e.shiftKey)
			}
		}
	}

	/** Displays the lobby page */
	def displayLobby(name: String): Unit = {
		lobby.classList.add("visible")
		playerName.textContent = name
		button.textContent = SearchForGame
	}

	/** Starts searching for a game */
	private def startSearch(fast: Boolean): Unit = {
		button.textContent = CancelSearch
		searching = true
		lobby.classList.add("searching")
		Server.searchGame(playerName.textContent, fast)
		setupSearchStats()
	}

	/** Stops searching for a game */
	private def stopSearch(): Unit = {
		button.textContent = SearchForGame
		searching = false
		lobby.classList.remove("searching")
		js.timers.clearInterval(statsInterval)
		statsInterval = null
		Server.disconnect()
	}

	/** Setups the search stats display */
	private def setupSearchStats(): Unit = {
		searchStart = js.Date.now()
		playersInQueue = 0
		statsInterval = js.timers.setInterval(1000) { updateSearchStats() }
		updateSearchStats()
	}

	/** Updates the search stats display with current values */
	private def updateSearchStats(): Unit = {
		val time = (js.Date.now() - searchStart).toInt / 1000
		// Dots
		val dotsCounts = time % 3 + 1
		dots.innerHTML = "." * dotsCounts + "&nbsp;" * (3 - dotsCounts)
		// Clock
		def pad(value: Int): String = if (value < 10) s"0$value" else s"$value"
		val clock = s"${pad(time / 60)}:${pad(time % 60)}"
		// Queue length
		val players = if (playersInQueue == 1) "player" else "players"
		stats.innerHTML = s"$clock &ndash; $playersInQueue $players in queue"
	}

	/** A game was found */
	def gameFound(mode: GameMode, teams: Seq[TeamInfo], me: UID, warmup: Int): Unit = {
		js.timers.clearInterval(statsInterval)
		statsInterval = null
		found = true
		lobby.classList.remove("searching")
		lobby.classList.add("found")
		js.timers.setTimeout(2000) {
			lobby.classList.remove("found")
			lobby.classList.remove("visible")
			found = false
			searching = false
			Intro.display(mode, teams, me, warmup - 2)
		}
	}

	/** Handle lobby messages from server */
	def message(lm: ServerMessage.LobbyMessage): Unit = lm match {
		case ServerMessage.QueueUpdate(length) => playersInQueue = length
		case ServerMessage.GameFound(mode, teams, me, warmup) => gameFound(mode, teams, me, warmup)
		case ServerMessage.GameStart =>
			for (node <- dom.document.querySelectorAll(".visible"); elem = node.asInstanceOf[html.Element]) {
				elem.classList.remove("visible")
				elem.classList.remove("fade-out")
			}
		case ServerMessage.GameEnd =>
			Server.disconnect()
			displayLobby(playerName.textContent)
	}
}
