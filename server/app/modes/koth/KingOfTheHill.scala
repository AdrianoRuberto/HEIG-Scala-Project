package modes
package koth

import akka.actor.Props
import game.GameMode

object KingOfTheHill extends GameBuilder.Standard(
	mode = GameMode.KingOfTheHill,
	spots = _ => 8,
	game = teams => Props(new Game(teams)),
	bot = Props[Bot]
)
