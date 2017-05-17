package game

import game.Lobby.displayLobby
import org.scalajs.dom
import scala.scalajs.js.JSApp

object App extends JSApp {
	private lazy val loader = dom.document.querySelector("#loader")

	/**
	  * Application entry point.
	  */
	def main(): Unit = dom.window.on(Event.Load)(_ => {
		loader.classList.add("fade-out")

		loader.on(Event.TransitionEnd) { _ =>
			loader.parentNode.removeChild(loader)
			dom.window.sessionStorage.getItem("username") match {
				case null => Login.requestUsername()
				case name => displayLobby(name)
			}
		}

		Lobby.setup()
		Login.setup()
	})
}
