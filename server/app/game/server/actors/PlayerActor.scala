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
import scala.concurrent.duration._
import utils.Debug

class PlayerActor @Inject() (@Assisted queue: SourceQueue[Array[Byte]], @Assisted queueSize: Int)
                            (@Named("matchmaker") mm: ActorRef)
		extends Actor with RequiresMessageQueue[UnboundedControlAwareMailbox] {
	import context._

	/** The game actor */
	private var game: ActorRef = null

	/** Outgoing message buffer to handle client back-pressure */
	private var buffer: mutable.Buffer[ServerMessage] = mutable.Buffer.empty

	/** Whether there is a queue offer pending */
	private var pending: Int = 0

	/** Latency of this client */
	private var latency: Double = 0.0

	/** Whether a ping request is currently pending */
	private var pingPending: Boolean = false

	// Schedule the first ping tick
	system.scheduler.scheduleOnce(500.millis, self, PlayerActor.PingTick)

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
		case PlayerActor.OfferAck => out.ack()
		case PlayerActor.PingTick => pingTick()
		case buffer: Array[Byte] => handleMessage(buffer)
		case msg: ServerMessage.GameFound =>
			out ! msg
			sender() ! Matchmaker.Ready
		case msg: ServerMessage => out ! msg
		case Matchmaker.Bind(ref) => game = ref
		case unknown => out ! Debug.error(s"Unknown message: $unknown")
	}

	private def handleMessage(buffer: Array[Byte]): Unit = {
		Unpickle[ClientMessage].fromBytes(ByteBuffer.wrap(buffer)) match {
			case ClientMessage.Ping(payload) =>
				pingReceive(payload)
			case ClientMessage.SearchGame(name, fast) =>
				mm ! Matchmaker.Register(PlayerInfo(UID.next, name, bot = false), fast)
			case msg: ClientMessage.GameMessage =>
				if (game == null) out ! Debug.warn(s"Ignored GameMessage because socket is not yet bound: $msg")
				else game ! msg
		}
	}

	private def pingTick(): Unit = {
		if (!pingPending) {
			pingPending = true
			out ! ServerMessage.Ping(latency, System.nanoTime())
		}
		system.scheduler.scheduleOnce(500.millis, self, PlayerActor.PingTick)
	}

	private def pingReceive(payload: Long): Unit = {
		pingPending = false
		val delta = System.nanoTime() - payload
		// Convert nanos to millis and divide by 2 to estimate one-way latency
		val ms = delta / 2000000.0
		// Smooth latency variations by only updating a third of the old value
		latency = (latency * 2 + ms) / 3
		if (game != null) game ! PlayerActor.UpdateLatency(latency)
	}
}

object PlayerActor {
	case object OfferAck extends ControlMessage
	case object PingTick
	case class UpdateLatency(latency: Double)

	trait Factory {
		def apply(out: SourceQueue[Array[Byte]], queueSize: Int): Actor
	}
}
