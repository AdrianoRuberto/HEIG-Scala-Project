package game.server

import engine.geometry.Shape
import game.UID

/**
  * A Region allows the game implementation to be notified of players entering
  * or leaving a predefined area.
  *
  * @param shape the region shape
  */
abstract class Region (val shape: Shape) {
	/** The set of players inside the region at the end of the last tick, used to diff computation */
	private[server] var inside: Set[UID] = Set.empty

	/** A player enters the region */
	private[server] def playerEnters(uid: UID): Unit

	/** A player leaves the region*/
	private[server] def playerExits(uid: UID): Unit

	/** Check whether a player is accepted by the region filter */
	private[server] def playerAccepted(uid: UID): Boolean

	/** The set of UIDs of players currently inside the region */
	def players: Set[UID] = inside

	/**
	  * Removes the region.
	  *
	  * The game implementation will stop being notified and an exit event will
	  * be generated for every player still in the region.
	  */
	def remove(): Unit
}
