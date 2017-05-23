package engine
package entity
package feature

import engine.geometry.Point

trait Positioned extends Entity {
	def position: Point
}
