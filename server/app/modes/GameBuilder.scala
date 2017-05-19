package modes

import akka.actor.{ActorRef, ActorSystem}
import game.{GameMode, TeamInfo, UID}
import modes.ctf.CaptureTheFlag
import modes.koth.KingOfTheHill
import scala.util.Random

abstract class GameBuilder(val mode: GameMode) {
	def playerSpots(queueSize: Int): Int
	def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam]
	def instantiate(players: Seq[GameTeam])(implicit as: ActorSystem): ActorRef
	def spawnBot()(implicit as: ActorSystem): ActorRef

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
}
