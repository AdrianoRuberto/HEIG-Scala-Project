package game.server

abstract class Ticker {
	def tick(dt: Double): Unit
	def unregister(): Unit
}
