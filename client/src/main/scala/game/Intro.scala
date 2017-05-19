package game

import org.scalajs.dom
import org.scalajs.dom.html
import scala.scalajs.js

object Intro {
	private lazy val intro = dom.document.querySelector("#intro")
	private lazy val modeName = dom.document.querySelector("#intro h2")
	private lazy val modeDesc = dom.document.querySelector("#intro p")
	private lazy val teamsList = dom.document.querySelector("#intro #teams")
	private lazy val loadBar = dom.document.querySelector("#intro #inner").asInstanceOf[html.Div]

	private def div(cls: String, text: js.UndefOr[String] = js.undefined): dom.Element = {
		val div = dom.document.createElement("div")
		div.classList.add(cls)
		for (t <- text) div.textContent = t
		div
	}

	def display(mode: GameMode, teams: Seq[TeamInfo], me: UID, warmup: Double): Unit = {
		modeName.textContent = mode.name
		modeDesc.textContent = mode.desc
		teamsList.innerHTML = ""
		dom.window.requestAnimationFrame(updateTimer(js.Date.now(), warmup) _)
		intro.classList.add("visible")
		for (team <- teams) {
			val block = div("team")
			block.appendChild(div("name", team.name))
			for (player <- team.players) {
				val name = if (player.bot) "*" + player.name else player.name
				val entry = div("player", name)
				if (player.uid == me) {
					entry.classList.add("me")
					block.classList.add("mine")
				}
				if (player.bot) entry.classList.add("bot")
				block.appendChild(entry)
			}
			teamsList.appendChild(block)
		}
	}

	def updateTimer(start: Double, warmup: Double)(timer: Double): Unit = {
		val now = js.Date.now()
		val progress = (now - start) / (warmup * 1000)
		loadBar.style.width = if (progress >= 1.0) "100%" else s"${progress * 100}%"
		if (progress <= 1) dom.window.requestAnimationFrame(updateTimer(start, warmup) _)
	}
}
