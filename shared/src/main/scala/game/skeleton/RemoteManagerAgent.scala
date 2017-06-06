package game.skeleton

/**
  * Interface to a remote skeleton manager.
  */
trait RemoteManagerAgent {
	/** Sends the event message to the remote skeleton manager */
	def send (event: ManagerEvent): Unit

	/**
	  * Alternative version of [[send]] allowing the node to access estimated
	  * transmission latency while constructing the event message.
	  *
	  * This is notably used by [[game.skeleton.node.InterpolatedNode]] to
	  * compensate for socket latency while sending interpolation updates.
	  *
	  * The builder function will be called once for every remote client and will
	  * be given the estimated transmission latency for the given client.
	  *
	  * @param f a function constructing event messages from estimated latency
	  */
	def sendLatencyAware (f: Double => ManagerEvent): Unit
}
