package game.server

import game.UID
import game.skeleton.AbstractSkeleton
import scala.language.implicitConversions

trait DoodadInstance {
	val game: BasicGame
	val uid: UID
	def remove(): Unit = game.removeDoodad(uid)
}

object DoodadInstance {
	case class Static(game: BasicGame, uid: UID) extends DoodadInstance {
		def withSkeleton[T <: AbstractSkeleton](skeleton: T): Dynamic[T] = Dynamic(game, uid, skeleton)
	}

	case class Dynamic[T <: AbstractSkeleton](game: BasicGame, uid: UID, skeleton: T) extends DoodadInstance

	implicit def DynamicSkeleton[T <: AbstractSkeleton](dyn: Dynamic[T]): T = dyn.skeleton
}
