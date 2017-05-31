package game.skeleton

abstract class Transmitter {
	def ! (event: ManagerEvent): Unit
	def sendLatencyAware (f: Double => ManagerEvent): Unit
}

object Transmitter {
	object NoTransmitter extends Transmitter {
		def ! (event: ManagerEvent): Unit = fail
		def sendLatencyAware (f: (Double) => ManagerEvent): Unit = fail
		def fail: Nothing =
			throw new IllegalStateException("Attempt to transmit skeleton event through Transmitter.NoTransmitter")
	}
}
