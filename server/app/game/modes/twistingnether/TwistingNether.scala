package game.modes
package twistingnether

import akka.actor.Props
import game.shared.GameMode

object TwistingNether extends GameBuilder.Standard(
	mode = GameMode.TwistingNether,
	spots = _ => 2,
	game = teams => Props(new TwistingNetherGame(teams)),
	bot = name => Props(new TwistingNetherBot(name)),
	warmupTime = _ => 4
)
