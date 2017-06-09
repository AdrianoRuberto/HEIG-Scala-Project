package game.server

import game.UID
import game.skeleton.AbstractSkeleton
import scala.language.implicitConversions

/**
  * Base trait for an instance of a Doodad.
  */
sealed trait DoodadInstance {
	/** UID for this doodad */
	val uid: UID

	/** Removes this doodad */
	def remove(): Unit
}

object DoodadInstance {
	/**
	  * An instance of a static doodad.
	  *
	  * @param uid the doodad UID
	  */
	abstract class Static (val uid: UID) extends DoodadInstance {
		/**
		  * Extends this static doodad by linking it to a skeleton instance,
		  * making it a dynamic doodad instance.
		  *
		  * @param skeleton the skeleton bound to this doodad
		  * @tparam T the type of skeleton used
		  */
		def withSkeleton[T <: AbstractSkeleton](skeleton: T): Dynamic[T] = new Dynamic[T](uid, skeleton) {
			def remove(): Unit = {
				skeleton.collect()
				Static.this.remove()
			}
		}
	}

	/**
	  * An instance of a dynamic doodad with its associated skeleton instance.
	  *
	  * This class of doodad instance cannot be instantiated directly. Instead,
	  * the [[Static.withSkeleton]] method should be used to attach a skeleton
	  * instance to an already existing [[Static]] instance. This mirrors the
	  * internal logic of [[BasicGame]] implementation and shouldn't be a
	  * concern for user code using the factory provided by [[BasicGame]].
	  *
	  * @param uid      the doodad UID
	  * @param skeleton the skeleton instance associated with this doodad
	  * @tparam T the type of the skeleton used
	  */
	abstract class Dynamic[T <: AbstractSkeleton] private[DoodadInstance] (val uid: UID, val skeleton: T) extends DoodadInstance

	/** Implicitly extracts the skeleton object from a DoodadInstance.Dynamic */
	@inline implicit def DynamicSkeleton[T <: AbstractSkeleton](dyn: Dynamic[T]): T = dyn.skeleton
}
