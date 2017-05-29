package game.server.modes.dummy

import game.maps.GameMap
import game.server.actors.Watcher
import game.server.{BasicGame, GameTeam}
import scala.concurrent.duration._

class DummyGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = ()

	def start(): Unit = {
		loadMap(GameMap.Illios)
		context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case _ =>
	}
}
