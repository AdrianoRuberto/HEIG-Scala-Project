package game.server.modes.koth

import akka.actor.Props
import game.server.GameBuilder
import game.server.modes.dummy.{DummyBot, DummyGame}
import game.shared.GameMode

object KingOfTheHillBuilder extends GameBuilder.Standard(
	mode = GameMode.KingOfTheHill,
	spots = _ => 8,
	game = teams => Props(new DummyGame(teams)),
	bot = name => Props(new DummyBot(name))
)
