package game.server.modes.ctf

import akka.actor.Props
import game.GameMode
import game.server.GameBuilder
import game.server.modes.dummy.DummyBot

object CaptureTheFlagBuilder extends GameBuilder.Standard(
	mode = GameMode.CaptureTheFlag,
	spots = GameBuilder.defaultSpots,
	game = teams => Props(new CaptureTheFlagGame(teams)),
	bot = name => Props(new DummyBot(name))
)
