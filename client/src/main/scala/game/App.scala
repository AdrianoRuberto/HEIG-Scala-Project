package game

import game.Lobby.displayLobby
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.timers.SetTimeoutHandle

object App extends JSApp {
	private lazy val loader = dom.document.querySelector("#loader")
	private lazy val cover = dom.document.querySelector("#cover")

	private var timers = Set.empty[SetTimeoutHandle]

	/**
	  * Application entry point.
	  */
	def main(): Unit = dom.window.on(Event.Load)(_ => {
		loader.classList.add("fade-out")
		App.timeout(1250) {
			loader.parentNode.removeChild(loader)
			boot()
		}

		Game.setup()
		Lobby.setup()
		Login.setup()
	})

	def boot(): Unit = {
		cover.classList.add("visible")
		dom.window.sessionStorage.getItem("username") match {
			case null => Login.requestUsername()
			case name => displayLobby(name)
		}
	}

	def timeout(duration: Double)(body: => Unit): Unit = {
		var handle: SetTimeoutHandle = null
		handle = js.timers.setTimeout(duration) {
			timers -= handle
			body
		}
		timers += handle
	}

	def reboot(failure: Boolean = false): Unit = {
		timers.foreach(js.timers.clearTimeout)
		timers = timers.empty
		hidePanels()
		Server.disconnect(true)
		Game.stop()
		if (failure) {
			Toast.show("Your game was shut down due to an unexpected server error")
		}
		boot()
	}

	def hidePanels(): Unit = {
		for (node <- dom.document.querySelectorAll(".visible, .fade-out"); elem = node.asInstanceOf[html.Element]) {
			elem.classList.remove("visible")
			elem.classList.remove("fade-out")
		}
		Lobby.reset()
	}
}
