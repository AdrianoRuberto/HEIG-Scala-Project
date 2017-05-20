package actors

import akka.actor._
import game.ServerMessage
import play.api.libs.json._

/**
  * A Watcher is an actor supervising every actors from a instance of a game.
  *
  * It provides a security layer catching any exception thrown during the game,
  * sends it to players' browser and terminate everything cleanly.
  *
  * The matchmaker instantiate this actor and then asks it to instantiate more children.
  *
  * @param actors the human actors in this game
  */
class Watcher private (actors: Seq[ActorRef]) extends Actor {
	/** Number of connected human players */
	private var connected: Int = actors.size

	/** Initial behavior until every actors are instantiated */
	def receive: Receive = {
		// Instantiate a new actor for the matchmaker
		case Watcher.Instantiate(props) => sender ! context.actorOf(props)

		// The matchmaker is done talking to us, start monitoring
		case Watcher.Ready =>
			actors.foreach(context.watch)
			context.become(ready)
	}

	/** Monitor game status */
	def ready: Receive = {
		// Track human player disconnects; if no more humans are connected, shutdown the game
		case Terminated(_) =>
			connected -= 1
			if (connected == 0) context.stop(self)

		// Gracefully terminates game
		case Watcher.Terminate =>
			for (actor <- actors) {
				actor ! ServerMessage.GameEnd
				actor ! PoisonPill
			}
			context.stop(self)
	}

	/** In case of failure, the watcher will terminate everything related to this game */
	override def supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
		case e: Throwable =>
			val msg = ServerMessage.JsonError(encode(e).toString())
			for (actor <- actors) {
				actor ! msg
				actor ! ServerMessage.ServerError
				actor ! PoisonPill
			}
			context.stop(self)
			SupervisorStrategy.Stop
	}

	/** Encodes an error as JSON */
	def encode(throwable: Throwable): JsValue = {
		if (throwable == null) JsNull
		else Json.obj(
			"1-class" -> throwable.getClass.getName,
			"2-message" -> throwable.getMessage,
			"3-stack" -> JsArray(throwable.getStackTrace.map(_.toString).map(JsString.apply)),
			"4-cause" -> encode(throwable.getCause)
		)
	}
}

object Watcher {
	case class Instantiate(props: Props)
	case object Terminate
	case object Ready

	def boundTo(actor: ActorRef)(implicit as: ActorSystem): ActorRef = boundTo(Seq(actor))
	def boundTo(actors: Seq[ActorRef])(implicit as: ActorSystem): ActorRef = {
		as.actorOf(Props(new Watcher(actors)))
	}
}
