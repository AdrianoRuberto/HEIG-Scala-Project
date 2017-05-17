package actors

import akka.actor.{Actor, ActorRef}
import boopickle.DefaultBasic._
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Named
import game.ServerMessage.GameFound
import game.{ClientMessage, ServerMessage}
import java.nio.ByteBuffer

class PlayerSocket @Inject() (@Assisted socket: ActorRef)
                             (@Named("matchmaker") mm: ActorRef) extends Actor {
	def receive: Receive = {
		case buffer: Array[Byte] =>
			val msg = Unpickle[ClientMessage].fromBytes(ByteBuffer.wrap(buffer))
			handleMessage(msg)
		case unknown =>
			throw new IllegalArgumentException(s"Unknown message: $unknown")
	}

	object out {
		def ! (msg: ServerMessage): Unit = {
			socket ! Pickle.intoBytes(msg).array()
		}
	}

	def handleMessage(msg: ClientMessage): Unit = msg match {
		case ClientMessage.SearchGame(player) =>
			println(player.name, "is searching a game")
			out ! GameFound(null)
	}
}

object PlayerSocket {
	trait Factory {
		def apply(out: ActorRef): Actor
	}
}
