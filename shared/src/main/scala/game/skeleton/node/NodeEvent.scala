package game.skeleton.node

sealed trait NodeEvent

object NodeEvent {
	sealed trait SimpleEvent extends NodeEvent
	case class SimpleUpdate(value: Array[Byte]) extends SimpleEvent

	sealed trait InterpolatedEvent extends NodeEvent
	case class InterpolatedUpdate(target: Double, duration: Double) extends InterpolatedEvent
}
