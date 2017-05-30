package game.skeleton

abstract class Transmitter {
	def ! (event: Event.ManagerEvent): Unit
}

object Transmitter {
	object NoTransmitter extends Transmitter {
		def ! (event: Event.ManagerEvent): Unit =
			throw new IllegalStateException("Attempt to transmit skeleton event through Transmitter.NoTransmitter")
	}
}
