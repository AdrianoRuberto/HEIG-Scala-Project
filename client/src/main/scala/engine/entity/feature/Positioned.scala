package engine
package entity
package feature

import engine.utils.Point

trait Positioned extends Entity {
	def position: Point
}
