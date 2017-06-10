package game.server

import akka.actor.Props
import game.server.modes.ctf.CaptureTheFlagBuilder
import game.server.modes.koth.KingOfTheHillBuilder
import game.{GameMode, TeamInfo, UID}
import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Random

abstract class GameBuilder(val mode: GameMode) {
	def playerSpots(queueSize: Int, longest: FiniteDuration): Int
	def warmup(players: Int): Int

	def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam]
	def gameProps(players: Seq[GameTeam]): Props
	def botProps(name: String): Props

	protected def randomTeams(players: Seq[GamePlayer], teams: Int): Seq[GameTeam] = {
		val total = players.length
		require(total % teams == 0, s"Impossible to split $total players between $teams teams")
		Random.shuffle(players).grouped(total / teams).toVector.zip(1 to 9)
				.map {
					case (members, nb) =>
						val team = TeamInfo(UID.next, s"Team $nb", members.map(_.info))
						GameTeam(team, members)
				}
	}
}

object GameBuilder {
	val modes: Vector[GameBuilder] = Vector(CaptureTheFlagBuilder, KingOfTheHillBuilder)
	def random: GameBuilder = modes(Random.nextInt(modes.size))

	abstract class Standard (mode: GameMode, spots: (Int, FiniteDuration) => Int,
	                         game: (Seq[GameTeam]) => Props, bot: (String) => Props,
	                         warmupTime: (Int) => Int = (_) => 10,
	                         teams: (Int) => Int = (_) => 2) extends GameBuilder(mode) {
		def playerSpots(queueSize: Int, longest: FiniteDuration): Int = spots(queueSize, longest)
		def warmup(players: Int): Int = warmupTime(players)
		def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam] = randomTeams(players, teams(players.size))
		def gameProps(teams: Seq[GameTeam]): Props = game(teams)
		def botProps(name: String): Props = bot(name)
	}

	def defaultSpots(count: Int, longest: FiniteDuration): Int = {
		if (longest > 60.seconds && count < 4) 2
		else if (longest > 45.seconds && count < 6) 4
		else if (longest > 30.seconds && count < 8) 6
		else 8
	}
}
