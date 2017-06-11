package game.server.modes.koth

import akka.actor.Props
import game.GameMode
import game.server.GameBuilder
import game.server.modes.dummy.DummyBot

object KingOfTheHillBuilder extends GameBuilder.Standard(
	mode = GameMode.KingOfTheHill,
	spots = GameBuilder.defaultSpots,
	game = teams => Props(new KingOfTheHillGame(teams)),
	bot = name => Props(new DummyBot(name))
)
