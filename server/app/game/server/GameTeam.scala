package game.server

import game.TeamInfo

case class GameTeam(info: TeamInfo, players: Seq[GamePlayer])
