package game.server

import akka.actor.{Actor, ActorRef}
import game.protocol.ServerMessage
import utils.Debug

abstract class BasicActor(name: String) extends Actor {
	import context._

	/** The ActorRef to the watcher actor */
	val watcher: ActorRef = parent

	// Debug
	private val debugPrefix: Debug.Serializable = s"[$name]"
	private def console(fn: (Seq[Debug.Serializable]) => ServerMessage.Debug, args: Seq[Debug.Serializable]): Unit = {
		watcher ! fn(debugPrefix +: args)
	}
	def debug(args: Debug.Serializable*): Unit = console(Debug.verbose, args)
	def log(args: Debug.Serializable*): Unit = console(Debug.info, args)
	def warn(args: Debug.Serializable*): Unit = console(Debug.warn, args)
	def error(args: Debug.Serializable*): Unit = console(Debug.error, args)
}
