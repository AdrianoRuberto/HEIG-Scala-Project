package game

import game.Lobby.displayLobby
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.JSApp

object App extends JSApp {
	private lazy val loader = dom.document.querySelector("#loader")

	/**
	  * Application entry point.
	  */
	def main(): Unit = dom.window.on(Event.Load)(_ => {
		loader.classList.add("fade-out")
		js.timers.setTimeout(1250) {
			loader.parentNode.removeChild(loader)
			boot()
		}

		Game.setup()
		Lobby.setup()
		Login.setup()
	})

	def boot(): Unit = {
		dom.window.sessionStorage.getItem("username") match {
			case null => Login.requestUsername()
			case name => displayLobby(name)
		}
	}

	def reboot(): Unit = {
		Toast.show("Your game was shut down due to an unexpected server error")
		boot()
	}
}
