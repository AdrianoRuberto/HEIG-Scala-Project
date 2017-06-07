package game

import macros.pickle

@pickle case class TeamInfo(uid: UID, name: String, players: Seq[PlayerInfo])
