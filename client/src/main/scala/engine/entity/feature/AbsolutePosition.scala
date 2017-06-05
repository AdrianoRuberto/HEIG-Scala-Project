package engine.entity.feature

import engine.entity.Entity

trait AbsolutePosition extends Entity {
	protected final val Center = Double.NaN
	override def positionIsAbsolute: Boolean = true
}
