package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated, Status => AkkaStatus}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import game.server.actors.PlayerActor
import javax.inject._
import play.api.mvc._

@Singleton
class HomeController @Inject() (playerFactory: PlayerActor.Factory)
                               (implicit as: ActorSystem, mat: Materializer)
		extends Controller {

	private final val QueueSize = 16

	def index = Action { implicit request =>
		Ok(views.html.main())
	}

	def socket = WebSocket.accept[Array[Byte], Array[Byte]] { req =>
		val (out, publisher) = Source.queue[Array[Byte]](QueueSize, OverflowStrategy.backpressure)
				.toMat(Sink.asPublisher(false))(Keep.both).run()

		Flow.fromSinkAndSource(
			Sink.actorRef(as.actorOf(Props(new Actor {
				val flowActor: ActorRef = context.watch(context.actorOf(Props(playerFactory(out, QueueSize)), "flowActor"))

				def receive: Receive = {
					case AkkaStatus.Success(_) | AkkaStatus.Failure(_) => flowActor ! PoisonPill
					case Terminated(_) => context.stop(self)
					case other => flowActor ! other
				}

				override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
					case _ => SupervisorStrategy.Stop
				}
			})), AkkaStatus.Success(())),
			Source.fromPublisher(publisher)
		)
	}
}
