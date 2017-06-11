package game.server.modes.twistingnether

import engine.geometry.Vector2D
import game.UID
import game.server.behaviors.StandardDeathBehavior
import game.server.{BasicGame, GameMap, GameTeam}

class TwistingNetherGame (roster: Seq[GameTeam]) extends BasicGame(roster) with StandardDeathBehavior {
	private val map = GameMap.Nepal

	loadMap(map)
	setDefaultTeamColors()
	setDefaultCamera()

	def respawnLocation(player: UID): Vector2D = map.spawns(teamsIndex(player.team))
}
