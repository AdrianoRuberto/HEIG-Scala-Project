package game.modes
package ctf

import akka.actor.Props
import game.modes.dummy.{DummyBot, DummyGame}
import game.shared.GameMode

object CaptureTheFlag extends GameBuilder.Standard(
	mode = GameMode.CaptureTheFlag,
	spots = _ => 8,
	game = teams => Props(new DummyGame(teams)),
	bot = Props[DummyBot]
)
