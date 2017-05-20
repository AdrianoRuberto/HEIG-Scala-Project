package modes
package twistingnether

import akka.actor.Props
import game.GameMode

object TwistingNether extends GameBuilder.Standard(
	mode = GameMode.TwistingNether,
	spots = _ => 2,
	game = teams => Props(new Game(teams)),
	bot = Props[Bot],
	warmupTime = _ => 4
)
