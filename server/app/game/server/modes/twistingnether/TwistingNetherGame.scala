package game.server.modes.twistingnether

import game.maps.GameMap
import game.server.{BasicGame, GameTeam}
import game.spells.Spell

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) {

	log("TN: Game init")
	loadMap(GameMap.Illios)
	setDefaultTeamColors()
	camera.followSelf()
	camera.setSpeed(250)

	for (player <- playersUID) {
		player gainSpell (0, Spell.Sword)
		player gainSpell (3, Spell.Sprint)
	}

	def start(): Unit = {
		log("TN: Game start")

		//warn("FIXME: Game will shutdown in 5 seconds")
		//context.system.scheduler.scheduleOnce(5.seconds, parent, Watcher.Terminate)
	}

	def message: Receive = {
		case null => ???
	}

	def tick(dt: Double): Unit = ()
}
