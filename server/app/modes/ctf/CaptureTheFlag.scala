package modes
package ctf

import game.{GameMode, Player, Team}

object CaptureTheFlag extends GameBuilder(GameMode.CaptureTheFlag, 6, None) {
	def compose(players: Seq[Player]): Seq[Team] = randomTeams(players, 2)
}
