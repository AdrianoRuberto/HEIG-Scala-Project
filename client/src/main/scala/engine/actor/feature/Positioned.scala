package engine
package actor
package feature

import engine.utils.Point

trait Positioned extends Actor {
	def position: Point
}
