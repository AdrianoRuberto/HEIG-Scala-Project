package game.server.modes.dummy

import game.maps.GameMap
import game.server.actors.Watcher
import game.server.{BasicGame, GameTeam}
import scala.concurrent.duration._

class DummyGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	import context._

	def init(): Unit = {
		loadMap(GameMap.Illios)
		camera.followSelf()
		setDefaultTeamColors()
	}

	def start(): Unit = {
		context.system.scheduler.scheduleOnce(30.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case _ =>
	}
}
