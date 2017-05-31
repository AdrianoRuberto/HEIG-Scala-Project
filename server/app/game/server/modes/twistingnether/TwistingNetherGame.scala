package game.server.modes.twistingnether

import game.maps.GameMap
import game.server.{BasicGame, GameTeam}

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {

	def init(): Unit = {
		log("TN: Game init")
		loadMap(GameMap.Illios)
		setDefaultTeamColors()
		camera.followSelf()
		camera.setSpeed(200)
	}

	def start(): Unit = {
		log("TN: Game start")

		//warn("FIXME: Game will shutdown in 5 seconds")
		//context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case null => ???
	}
}
