package game.server.actors

import akka.actor.{Actor, ActorRef}
import akka.dispatch.{ControlMessage, RequiresMessageQueue, UnboundedControlAwareMailbox}
import akka.stream.scaladsl.SourceQueue
import boopickle.DefaultBasic._
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Named
import game.protocol.{ClientMessage, ServerMessage}
import game.{PlayerInfo, UID}
import java.nio.ByteBuffer
import scala.collection.mutable
import utils.Debug

class PlayerActor @Inject() (@Assisted queue: SourceQueue[Array[Byte]], @Assisted queueSize: Int)
                            (@Named("matchmaker") mm: ActorRef)
		extends Actor with RequiresMessageQueue[UnboundedControlAwareMailbox] {
	import context._

	/** The game actor */
	private var game: ActorRef = context.system.deadLetters

	/** Outgoing message buffer to handle client back-pressure */
	private var buffer: mutable.Buffer[ServerMessage] = mutable.Buffer.empty

	/** Whether there is a queue offer pending */
	private var pending: Int = 0

	/** Fake actor for handling outgoing messages */
	private object out {
		def ! (msg: ServerMessage): Unit = {
			if (pending < queueSize) send(msg)
			else buffer += msg
		}

		def send(msg: ServerMessage): Unit = {
			pending += 1
			val bytes = Pickle.intoBytes(msg)
			val array = new Array[Byte](bytes.remaining)
			bytes.get(array)
			// Offer data to the output queue and wait for confirmation
			for (_ <- queue.offer(array)) self ! PlayerActor.OfferAck
		}

		def ack(): Unit = {
			pending -= 1
			buffer.size match {
				case 0 => // Buffer is empty! Yeah!
				case 1 =>
					send(buffer.head)
					buffer.clear()
				case _ =>
					send(ServerMessage.Bundle(buffer))
					buffer.clear()
			}
		}
	}

	def receive: Receive = {
		case buffer: Array[Byte] =>
			handleMessage(buffer)
		case PlayerActor.OfferAck =>
			out.ack()
		case msg: ServerMessage.GameFound =>
			out ! msg
			sender() ! Matchmaker.Ready
		case msg: ServerMessage =>
			out ! msg
		case Matchmaker.Bind(ref) =>
			game = ref
		case unknown =>
			out ! Debug.error(s"Unknown message: $unknown")
	}

	private def handleMessage(buffer: Array[Byte]): Unit = {
		Unpickle[ClientMessage].fromBytes(ByteBuffer.wrap(buffer)) match {
			case ClientMessage.Ping(payload) =>
				out ! ServerMessage.Ping(payload)
			case ClientMessage.SearchGame(name, fast) =>
				val player = PlayerInfo(UID.next, name, bot = false)
				mm ! Matchmaker.Register(player, fast)
			case msg: ClientMessage.GameMessage =>
				game ! msg
		}
	}
}

object PlayerActor {
	case object OfferAck extends ControlMessage

	trait Factory {
		def apply(out: SourceQueue[Array[Byte]], queueSize: Int): Actor
	}
}
