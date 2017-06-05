package game.server

import engine.geometry.Shape
import game.UID

abstract class Region (val shape: Shape) {
	private[server] var inside: Set[UID] = Set.empty
	private[server] def playerEnters(uid: UID): Unit
	private[server] def playerExits(uid: UID): Unit
	def remove(): Unit
}
