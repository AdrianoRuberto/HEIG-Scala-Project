package game.modes
package koth

import akka.actor.Props
import game.modes.dummy.{DummyBot, DummyGame}
import game.shared.GameMode

object KingOfTheHill extends GameBuilder.Standard(
	mode = GameMode.KingOfTheHill,
	spots = _ => 8,
	game = teams => Props(new DummyGame(teams)),
	bot = name => Props(new DummyBot(name))
)
