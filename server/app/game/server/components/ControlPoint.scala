package game.server.components

import engine.geometry.Shape
import game.UID
import game.server.BasicGame

case class ControlPoint (shape: Shape)(implicit game: BasicGame) {
	// Doodad
	//val skeleton: DynamicAreaSkeleton = game.createSkeleton(Skeleton.DynamicArea)
	//val doodad: DoodadInstance.Static = game.createDoodad(Doodad.Area.DynamicArea(skeleton.uid))

	// Region management
	game.createRegion(shape, enter, leave)

	def enter(player: UID): Unit = {

	}

	def leave(player: UID): Unit = {

	}

	def enable(): Unit = {

	}

	def disable(): Unit = {

	}
}
