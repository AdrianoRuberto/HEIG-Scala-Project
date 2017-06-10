package game.server.modes.koth

import engine.geometry.Vector2D
import game.UID
import game.server.behaviors.StandardDeathBehavior
import game.server.{BasicGame, GameTeam}

/**
  * Take control of the objective and defend it against the enemy team
  */
class KingOfTheHillGame (roster: Seq[GameTeam]) extends BasicGame(roster) with StandardDeathBehavior {
	def respawnLocationForPlayer(player: UID): Vector2D = Vector2D(0, 0)
}
