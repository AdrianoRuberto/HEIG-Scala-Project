package game.skeleton

import game.UID

abstract class Transmitter {
	def ! (event: Event.ClosetEvent): Unit
	final def ! (uid: UID, event: Event.SkeletonEvent): Unit = this ! Event.NotifySkeleton(uid, event)
}

object Transmitter {
	object NoTransmitter extends Transmitter {
		def ! (event: Event.ClosetEvent): Unit =
			throw new IllegalStateException("Attempt to transmit skeleton event through Transmitter.NoTransmitter")
	}
}
