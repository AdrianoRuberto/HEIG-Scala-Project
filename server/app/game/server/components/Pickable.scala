package game.server.components

import engine.geometry.Vector2D
import game.UID
import game.server.BasicGame

abstract class Pickable (persistant: Boolean)(implicit game: BasicGame) {
	val radius: Double
	val location: Vector2D

	def filter(uid: UID): Boolean = true

	val ticker = game.createTicker { dt =>

	}

	def remove(): Unit = {

	}
}
