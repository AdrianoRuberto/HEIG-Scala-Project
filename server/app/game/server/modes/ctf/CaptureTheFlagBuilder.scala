package game.server.modes.ctf

import akka.actor.Props
import game.server.GameBuilder
import game.server.modes.dummy.{DummyBot, DummyGame}
import game.shared.GameMode

object CaptureTheFlagBuilder extends GameBuilder.Standard(
	mode = GameMode.CaptureTheFlag,
	spots = _ => 8,
	game = teams => Props(new DummyGame(teams)),
	bot = name => Props(new DummyBot(name))
)
