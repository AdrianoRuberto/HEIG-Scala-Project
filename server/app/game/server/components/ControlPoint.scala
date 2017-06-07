package game.server.components

import engine.geometry.Shape
import game.UID
import game.doodads.Doodad
import game.doodads.area.DynamicAreaSkeleton
import game.server.BasicGame
import game.skeleton.Skeleton

case class ControlPoint (shape: Shape)(implicit game: BasicGame) {
	// Doodad
	val skeleton: DynamicAreaSkeleton = game.createGlobalSkeleton(Skeleton.DynamicArea)
	val doodad: UID = game.createGlobalDoodad(Doodad.Area.DynamicArea(skeleton.uid))

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
