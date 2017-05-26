package utils

import akka.actor.ActorRef

case class ActorGroup(private val targets: TraversableOnce[ActorRef]) extends AnyVal {
	def ! (msg: Any): Unit = for (target <- targets) target ! msg
}
