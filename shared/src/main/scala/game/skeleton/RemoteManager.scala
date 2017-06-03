package game.skeleton

trait RemoteManager {
	def send (event: ManagerEvent): Unit
	def sendLatencyAware (f: Double => ManagerEvent): Unit
}
