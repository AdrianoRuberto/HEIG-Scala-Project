package game.server.modes.koth

import akka.actor.Props
import game.GameMode
import game.server.GameBuilder
import game.server.modes.dummy.DummyBot
import game.server.modes.twistingnether.TwistingNetherGame

object KingOfTheHillBuilder extends GameBuilder.Standard(
	mode = GameMode.KingOfTheHill,
	spots = _ => 4,
	game = teams => Props(new TwistingNetherGame(teams)),
	bot = name => Props(new DummyBot(name))
)
