package game.modes

import akka.actor.{Actor, ActorRef}
import utils.Debug

abstract class BasicActor extends Actor {
	import context._

	/** The ActorRef to the watcher actor */
	val watcher: ActorRef = parent

	def log(args: Debug.Serializable*): Unit = watcher ! Debug.log(args: _*)
	def warn(args: Debug.Serializable*): Unit = watcher ! Debug.warn(args: _*)
	def error(args: Debug.Serializable*): Unit = watcher ! Debug.error(args: _*)
}
