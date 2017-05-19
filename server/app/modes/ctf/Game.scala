package modes
package ctf

import actors.Matchmaker
import akka.actor.Actor

class Game (teams: Seq[GameTeam]) extends Actor {
	def receive: Receive = {
		case Matchmaker.Start =>
		case _ =>
	}
}
