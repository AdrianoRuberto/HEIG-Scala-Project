package engine.entity.feature

trait AbsolutePosition extends Position {
	override def positionIsAbsolute: Boolean = true
}
