package game.server.modes.twistingnether

import akka.actor.Props
import game.GameMode
import game.server.GameBuilder
import game.server.modes.ctf.CaptureTheFlagGame

object TwistingNetherBuilder extends GameBuilder.Standard(
	mode = GameMode.CaptureTheFlag,
	spots = GameBuilder.defaultSpots,
	game = teams => Props(new CaptureTheFlagGame(teams)),
	bot = name => Props(new TwistingNetherBot(name)),
	warmupTime = _ => 3
)
