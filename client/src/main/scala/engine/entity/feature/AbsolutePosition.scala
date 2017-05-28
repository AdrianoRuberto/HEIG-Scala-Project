package engine.entity.feature

import engine.entity.Entity

trait AbsolutePosition extends Entity {
	override def positionIsAbsolute: Boolean = true
}
