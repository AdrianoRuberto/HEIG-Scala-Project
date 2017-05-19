package modes.koth

import akka.actor.Props
import game.GameMode
import modes.GameBuilder

object KingOfTheHill extends GameBuilder.Standard(
	mode = GameMode.KingOfTheHill,
	spots = _ => 8,
	game = teams => Props(new Game(teams)),
	bot = Props[Bot]
)
