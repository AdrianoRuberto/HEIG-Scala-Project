package game.server

import game.UID
import game.protocol.ServerMessage
import game.server.BasicGameImplicits.UIDOps
import game.skeleton.{Event, Transmitter}
import scala.language.implicitConversions

trait BasicGameImplicits {
	this: BasicGame =>

	/** Implicit reference to the BasicGame itself */
	protected implicit val self: this.type = this

	/**
	  * An instance of [[Transmitter]] that send [[Event.ClosetEvent]] to every players of the game.
	  * It is used as implicit parameter during the construction of the [[skeletons]] map.
	  */
	protected implicit object SkeletonTransmitter extends Transmitter {
		def ! (event: Event.ClosetEvent): Unit = {
			broadcast ! ServerMessage.SkeletonEvent(event)
		}
	}

	/** Implicit conversion from UID to UIDOps */
	protected implicit def uidToUIDOps(uid: UID): UIDOps = new UIDOps(uid)
}

object BasicGameImplicits {
	final class UIDOps(private val uid: UID) extends AnyVal {
		@inline def ! (msg: Any)(implicit game: BasicGame): Unit = game.actors.get(uid) match {
			case Some(ag) => ag ! msg
			case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
		}
	}
}
