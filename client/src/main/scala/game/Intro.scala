package game

import org.scalajs.dom
import scala.scalajs.js

object Intro {
	private lazy val intro = dom.document.querySelector("#intro")
	private lazy val modeName = dom.document.querySelector("#intro h2")
	private lazy val modeDesc = dom.document.querySelector("#intro p")
	private lazy val teamsList = dom.document.querySelector("#intro #teams")

	private def div(cls: String, text: js.UndefOr[String] = js.undefined): dom.Element = {
		val div = dom.document.createElement("div")
		div.classList.add(cls)
		for (t <- text) div.textContent = t
		div
	}

	def display(mode: GameMode, teams: Seq[TeamInfo], me: UID): Unit = {
		modeName.textContent = mode.name
		modeDesc.textContent = mode.desc
		teamsList.innerHTML = ""
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
}
