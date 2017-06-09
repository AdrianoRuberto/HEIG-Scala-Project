package game.server.modes.dummy

import game.maps.GameMap
import game.server.{BasicGame, GameTeam}

class DummyGame (roster: Seq[GameTeam]) extends BasicGame(roster) {

	loadMap(GameMap.Illios)
	players.camera.followSelf()
	setDefaultTeamColors()

	def start(): Unit = {
		//context.system.scheduler.scheduleOnce(30.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case _ =>
	}

	def tick(dt: Double): Unit = ()
}
