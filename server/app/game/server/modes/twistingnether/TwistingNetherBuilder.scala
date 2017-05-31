package game.server.modes.twistingnether

import akka.actor.Props
import game.protocol.enums.GameMode
import game.server.GameBuilder

object TwistingNetherBuilder extends GameBuilder.Standard(
	mode = GameMode.TwistingNether,
	spots = _ => 2,
	game = teams => Props(new TwistingNetherGame(teams)),
	bot = name => Props(new TwistingNetherBot(name)),
	warmupTime = _ => 4
)
