package game.server

import game.shared.TeamInfo

case class GameTeam(info: TeamInfo, players: Seq[GamePlayer])
