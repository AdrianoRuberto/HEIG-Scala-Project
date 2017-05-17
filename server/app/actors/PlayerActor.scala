package actors

import akka.actor.{Actor, ActorRef}
import boopickle.DefaultBasic._
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Named
import game.{ClientMessage, ServerMessage}
import java.nio.ByteBuffer

class PlayerActor @Inject() (@Assisted socket: ActorRef)
                            (@Named("matchmaker") mm: ActorRef) extends Actor {
	/** Fake actor for handling outgoing messages */
	object out {
		def ! (msg: ServerMessage): Unit = {
			socket ! Pickle.intoBytes(msg).array()
		}
	}

	def receive: Receive = {
		case buffer: Array[Byte] =>
			val msg = Unpickle[ClientMessage].fromBytes(ByteBuffer.wrap(buffer))
			handleMessage(msg)
		case msg: ServerMessage =>
			out ! msg
		case unknown =>
			out ! ServerMessage.Error(s"Unknown message: $unknown")
	}

	def handleMessage(msg: ClientMessage): Unit = msg match {
		case ClientMessage.SearchGame(player) => mm ! Matchmaker.Register(player)
	}
}

object PlayerActor {
	trait Factory {
		def apply(out: ActorRef): Actor
	}
}
