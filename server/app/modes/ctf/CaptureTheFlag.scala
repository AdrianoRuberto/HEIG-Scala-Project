package modes
package ctf

import akka.actor.{ActorRef, ActorSystem, Props}
import game.GameMode

object CaptureTheFlag extends GameBuilder(GameMode.CaptureTheFlag, 6) {
	def composeTeams(players: Seq[GamePlayer]): Seq[GameTeam] = randomTeams(players, 2)
	def spawnBot()(implicit as: ActorSystem): ActorRef = as.actorOf(Props[Bot])
	def instantiate(teams: Seq[GameTeam])(implicit as: ActorSystem): ActorRef = {
		as.actorOf(Props(new Game(teams)))
	}
}
