package game.server

import game.UID
import game.protocol.ServerMessage
import game.skeleton.concrete.CharacterSkeleton
import game.skeleton.{ManagerEvent, Transmitter}
import scala.language.implicitConversions

trait BasicGameImplicits {
	this: BasicGame =>

	/**
	  * An instance of [[Transmitter]] that send [[ManagerEvent]] to every players of the game.
	  * It is used as implicit parameter during the construction of the [[skeletons]] map.
	  */
	protected implicit object SkeletonTransmitter extends Transmitter {
		def ! (event: ManagerEvent): Unit = {
			broadcast ! ServerMessage.SkeletonEvent(event)
		}
		def sendLatencyAware (f: (Double) => ManagerEvent): Unit = {
			for ((uid, player) <- players; latency = latencies(uid)) {
				player.actor ! ServerMessage.SkeletonEvent(f(latency))
			}
		}
	}

	/** Some quality of life operations on UIDs */
	protected implicit final class UIDOps(private val uid: UID) {
		@inline def ! (msg: Any): Unit = actors.get(uid) match {
			case Some(ag) => ag ! msg
			case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
		}

		@inline def skeleton: CharacterSkeleton = skeletons(uid)
		@inline def latency: Double = latencies(uid)
	}
}
