package actors

import akka.actor.{Actor, ActorRef}
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import com.google.inject.name.Named

class PlayerSocket @Inject() (@Assisted out: ActorRef)
                             (@Named("matchmaker") mm: ActorRef) extends Actor {
	def receive: Receive = {
		case msg: Array[Byte] => out ! msg
	}
}

object PlayerSocket {
	trait Factory {
		def apply(out: ActorRef): Actor
	}
}
