package game.server.modes.ctf

import akka.actor.Props
import game.GameMode
import game.server.GameBuilder
import game.server.modes.dummy.DummyBot
import game.server.modes.twistingnether.TwistingNetherGame

object CaptureTheFlagBuilder extends GameBuilder.Standard(
	mode = GameMode.CaptureTheFlag,
	spots = GameBuilder.defaultSpots,
	game = teams => Props(new TwistingNetherGame(teams)),
	bot = name => Props(new DummyBot(name))
)
