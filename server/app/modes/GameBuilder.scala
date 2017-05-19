package modes

import game.{GameMode, Player, Team}
import modes.ctf.CaptureTheFlag
import scala.util.Random

abstract class GameBuilder(val mode: GameMode, val players: Int, val bot: Option[Any]) {
	def compose(players: Seq[Player]): Seq[Team]

	protected def randomTeams(players: Seq[Player], teams: Int): Seq[Team] = {
		val total = players.length
		require(total % teams == 0, s"Impossible to split $total players between $teams teams")
		Random.shuffle(players).grouped(total / teams).toSeq.zip('1' to '9')
				.map { case (plyrs, nb) => Team(s"Team $nb", plyrs) }
	}
}

object GameBuilder {
	val modes: Vector[GameBuilder] = Vector(CaptureTheFlag)
	def random: GameBuilder = modes(Random.nextInt(modes.size))
}
