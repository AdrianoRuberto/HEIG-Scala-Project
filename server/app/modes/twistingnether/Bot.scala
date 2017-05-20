package modes
package twistingnether

import akka.actor.{Actor, PoisonPill}
import game.ServerMessage

class Bot extends Actor {
	def receive: Receive = {
		case ServerMessage.GameEnd => self ! PoisonPill
	}
}
