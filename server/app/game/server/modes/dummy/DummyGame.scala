package game.server.modes.dummy

import game.server.{BasicGame, GameMap, GameTeam}

class DummyGame (roster: Seq[GameTeam]) extends BasicGame(roster) {
	loadMap(GameMap.Nepal)
	setDefaultCamera()
	setDefaultTeamColors()
}
