package game.server

import akka.actor.ActorRef
import game.shared.PlayerInfo

case class GamePlayer(actor: ActorRef, info: PlayerInfo)
