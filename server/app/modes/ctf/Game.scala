package modes
package ctf

import akka.actor.Actor

class Game (teams: Seq[GameTeam]) extends Actor {
	def receive: Receive = {
		case _ =>
	}
}
