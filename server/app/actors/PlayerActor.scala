package actors

import akka.actor.{Actor, ActorRef}
import boopickle.DefaultBasic._
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Named
import game.protocol.{ClientMessage, ServerMessage}
import game.shared.{PlayerInfo, UID}
import java.nio.ByteBuffer
import utils.Debug

class PlayerActor @Inject() (@Assisted socket: ActorRef)
                            (@Named("matchmaker") mm: ActorRef) extends Actor {
	/** The game actor */
	private var game: ActorRef = context.system.deadLetters

	/** Fake actor for handling outgoing messages */
	private object out {
		def ! (msg: ServerMessage): Unit = {
			val buffer = Pickle.intoBytes(msg)
			val array = new Array[Byte](buffer.remaining)
			buffer.get(array)
			socket ! array
		}
	}

	def receive: Receive = {
		case buffer: Array[Byte] =>
			val msg = Unpickle[ClientMessage].fromBytes(ByteBuffer.wrap(buffer))
			handleMessage(msg)
		case msg: ServerMessage =>
			out ! msg
		case Matchmaker.Bind(ref) =>
			game = ref
		case unknown =>
			out ! Debug.error(s"Unknown message: $unknown")
	}

	private def handleMessage(msg: ClientMessage): Unit = msg match {
		case ClientMessage.Ping(payload) =>
			out ! ServerMessage.Ping(payload)
		case ClientMessage.SearchGame(name, fast) =>
			val player = PlayerInfo(UID.next, name, bot = false)
			mm ! Matchmaker.Register(player, fast)
		case msg: ClientMessage.GameMessage =>
			game ! msg
	}
}

object PlayerActor {
	trait Factory {
		def apply(out: ActorRef): Actor
	}
}
