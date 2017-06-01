package game.client

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
	private val debug = false

	/**
	  * Application entry point.
	  */
	def main(): Unit = dom.window.on(Event.Load)(_ => {
		Game.setup()
		Lobby.setup()
		Login.setup()

		if (debug) {
			loader.parentNode.removeChild(loader)
			debugBoot()
		} else {
			loader.classList.add("fade-out")
			js.timers.setTimeout(1250) {
				loader.parentNode.removeChild(loader)
				boot()
			}
		}

		dom.document.on(Event.KeyDown) { ev =>
			if (!ev.repeat && ev.key == "a" && ev.ctrlKey) {
				Server.verbose.toggle()
				dom.console.log("Server verbose mode:", Server.verbose.value)
			}
		}
	})

	def boot(): Unit = {
		cover.classList.add("visible")
		dom.window.sessionStorage.getItem("username") match {
			case null => Login.requestUsername()
			case name => Lobby.displayLobby(name)
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
		Game.lock()
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

	def debugBoot(): Unit = ()
}
