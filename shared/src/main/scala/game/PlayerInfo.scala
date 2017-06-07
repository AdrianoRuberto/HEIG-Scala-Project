package game

import macros.pickle

@pickle
case class PlayerInfo(uid: UID, name: String, bot: Boolean)
