import actors.{Matchmaker, PlayerActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {
	def configure(): Unit = {
		bindActor[Matchmaker]("matchmaker")
		bindActorFactory[PlayerActor, PlayerActor.Factory]
	}
}
