package game.server

/**
  * An object that can be registered with a [[BasicGame]] to be called on
  * every game ticks. (Approximately 50 times per seconds)
  */
abstract class Ticker {
	/**
	  * Tick handler implementation.
	  *
	  * @param dt the elapsed time since the last tick
	  */
	def tick(dt: Double): Unit

	/** Removes this ticker preventing its [[tick]] function from being called again */
	def remove(): Unit
}
