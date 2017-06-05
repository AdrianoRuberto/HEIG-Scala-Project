package game.skeleton.node

import game.UID

sealed trait NodeEvent

object NodeEvent {
	sealed trait SimpleEvent extends NodeEvent
	case class SimpleUpdateBoolean(value: Boolean) extends SimpleEvent
	case class SimpleUpdateInt(value: Int) extends SimpleEvent
	case class SimpleUpdateDouble(value: Double) extends SimpleEvent
	case class SimpleUpdateString(value: String) extends SimpleEvent
	case class SimpleUpdateUID(value: UID) extends SimpleEvent
	case class SimpleUpdateGeneric(value: Array[Byte]) extends SimpleEvent

	sealed trait InterpolatedEvent extends NodeEvent
	case class InterpolatedUpdate(target: Double, duration: Double) extends InterpolatedEvent
}
