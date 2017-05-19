package modes.twistingnether

import actors.Matchmaker
import akka.actor.Actor
import modes.GameTeam

class Game (teams: Seq[GameTeam]) extends Actor {
	def receive: Receive = {
		case Matchmaker.Start =>
		case _ =>
	}
}
