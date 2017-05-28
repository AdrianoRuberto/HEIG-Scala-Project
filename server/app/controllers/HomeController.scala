package controllers

import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import game.server.actors.PlayerActor
import javax.inject._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

@Singleton
class HomeController @Inject() (playerFactory: PlayerActor.Factory)
                               (implicit as: ActorSystem, mat: Materializer)
		extends Controller {

	def index = Action { implicit request =>
		Ok(views.html.main())
	}

	def socket = WebSocket.accept[Array[Byte], Array[Byte]] { req =>
		ActorFlow.actorRef(out => Props(playerFactory(out)))
	}
}
