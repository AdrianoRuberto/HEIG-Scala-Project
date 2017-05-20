package modes

import akka.actor.Props
import game.{GameMode, TeamInfo, UID}
import modes.ctf.CaptureTheFlag
import modes.koth.KingOfTheHill
import scala.util.Random

abstract class GameBuilder(val mode: GameMode) {
	def playerSpots(queueSize: Int): Int
	def warmup(players: Int): Int

	def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam]
	def gameProps(players: Seq[GameTeam]): Props
	def botProps(): Props

	protected def randomTeams(players: Seq[GamePlayer], teams: Int): Seq[GameTeam] = {
		val total = players.length
		require(total % teams == 0, s"Impossible to split $total players between $teams teams")
		Random.shuffle(players).grouped(total / teams).toSeq.zip(1 to 9)
				.map {
					case (members, nb) =>
						val team = TeamInfo(UID.next, s"Team $nb", members.map(_.info))
						GameTeam(team, members)
				}
	}
}

object GameBuilder {
	val modes: Vector[GameBuilder] = Vector(CaptureTheFlag, KingOfTheHill)
	def random: GameBuilder = modes(Random.nextInt(modes.size))

	abstract class Standard (mode: GameMode, spots: (Int) => Int,
	                         game: (Seq[GameTeam]) => Props, bot: Props,
	                         warmupTime: (Int) => Int = _ => 10,
	                         teams: (Int) => Int = _ => 2) extends GameBuilder(mode) {
		def playerSpots(queueSize: Int): Int = spots(queueSize)
		def warmup(players: Int): Int = warmupTime(players)
		def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam] = randomTeams(players, teams(players.size))
		def gameProps(teams: Seq[GameTeam]): Props = game(teams)
		def botProps(): Props = bot
	}
}
