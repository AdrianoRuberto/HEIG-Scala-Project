package game.server.modes.dummy

import game.maps.GameMap
import game.server.{BasicGame, GameTeam}

class DummyGame (roster: Seq[GameTeam]) extends BasicGame(roster) {

	def init(): Unit = {
		loadMap(GameMap.Illios)
		camera.followSelf()
		setDefaultTeamColors()
	}

	def start(): Unit = {
		//context.system.scheduler.scheduleOnce(30.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case _ =>
	}
}
