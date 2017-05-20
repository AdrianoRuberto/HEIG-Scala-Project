package modes
package ctf

import akka.actor.Props
import game.GameMode

object CaptureTheFlag extends GameBuilder.Standard(
	mode = GameMode.CaptureTheFlag,
	spots = _ => 8,
	game = teams => Props(new Game(teams)),
	bot = Props[Bot]
)
