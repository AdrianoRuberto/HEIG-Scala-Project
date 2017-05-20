package actors

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorRef, ActorSystem, AllForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import game.ServerMessage
import play.api.libs.json._

class Watcher private (actors: Seq[ActorRef]) extends Actor {
	override def supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
		case e: Throwable =>
			val msg = ServerMessage.JsonError(encode(e).toString())
			for (actor <- actors) {
				actor ! msg
				actor ! ServerMessage.ServerError
				actor ! PoisonPill
			}
			context.stop(self)
			Resume
	}

	def encode(throwable: Throwable): JsValue = {
		if (throwable == null) JsNull
		else Json.obj(
			"1-class" -> throwable.getClass.getName,
			"2-message" -> throwable.getMessage,
			"3-stack" -> JsArray(throwable.getStackTrace.map(_.toString).map(JsString.apply)),
			"4-cause" -> encode(throwable.getCause)
		)
	}

	def receive: Receive = {
		case Watcher.Instantiate(props) => sender ! context.actorOf(props)
	}
}

object Watcher {
	case class Instantiate(props: Props)
	def reportingTo(actor: ActorRef)(implicit as: ActorSystem): ActorRef = reportingTo(Seq(actor))
	def reportingTo(actors: Seq[ActorRef])(implicit as: ActorSystem): ActorRef = {
		as.actorOf(Props(new Watcher(actors)))
	}
}
