package game.server

import game.UID
import game.protocol.ServerMessage
import game.skeleton.{Event, Transmitter}
import scala.language.implicitConversions
import utils.ActorGroup

trait BasicGameImplicits {
	this: BasicGame =>

	/**
	  * An instance of [[Transmitter]] that send [[Event.ClosetEvent]] to every players of the game.
	  * It is used as implicit parameter during the construction of the [[skeletons]] map.
	  */
	protected implicit object SkeletonTransmitter extends Transmitter {
		def ! (event: Event.ClosetEvent): Unit = {
			broadcast ! ServerMessage.SkeletonEvent(event)
		}
	}


	/** Implicit conversion from UID to ActorGroup, allowing to send messages to UIDs. */
	protected implicit def uidToActorGroup(uid: UID): ActorGroup = actors.get(uid) match {
		case Some(ag) => ag
		case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
	}
}
