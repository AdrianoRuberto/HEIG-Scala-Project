package modes.koth

import akka.actor.{ActorRef, ActorSystem, Props}
import game.GameMode
import modes.{GameBuilder, GamePlayer, GameTeam}

object KingOfTheHill extends GameBuilder(GameMode.KingOfTheHill) {
	def playerSpots(queueSize: Int): Int = 8
	def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam] = randomTeams(players, 2)
	def spawnBot()(implicit as: ActorSystem): ActorRef = as.actorOf(Props[Bot])
	def instantiate(teams: Seq[GameTeam])(implicit as: ActorSystem): ActorRef = {
		as.actorOf(Props(new Game(teams)))
	}
}
