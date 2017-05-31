package game.skeleton

abstract class Transmitter {
	def ! (event: Event.ManagerEvent): Unit
	def sendLatencyAware (f: Double => Event.ManagerEvent): Unit
}

object Transmitter {
	object NoTransmitter extends Transmitter {
		def ! (event: Event.ManagerEvent): Unit = fail
		def sendLatencyAware (f: (Double) => Event.ManagerEvent): Unit = fail
		def fail: Nothing =
			throw new IllegalStateException("Attempt to transmit skeleton event through Transmitter.NoTransmitter")
	}
}
