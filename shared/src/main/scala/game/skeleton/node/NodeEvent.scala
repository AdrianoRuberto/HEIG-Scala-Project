package game.skeleton.node

import game.UID
import macros.pickle
import utils.Color

@pickle sealed trait NodeEvent

object NodeEvent {
	sealed trait SimpleEvent extends NodeEvent
	@pickle case class SimpleUpdateBoolean(value: Boolean) extends SimpleEvent
	@pickle case class SimpleUpdateInt(value: Int) extends SimpleEvent
	@pickle case class SimpleUpdateDouble(value: Double) extends SimpleEvent
	@pickle case class SimpleUpdateString(value: String) extends SimpleEvent
	@pickle case class SimpleUpdateUID(value: UID) extends SimpleEvent
	@pickle case class SimpleUpdateColor(value: Color) extends SimpleEvent
	@pickle case class SimpleUpdateGeneric(value: Array[Byte]) extends SimpleEvent

	sealed trait InterpolatedEvent extends NodeEvent
	@pickle case class InterpolatedUpdate(target: Double, duration: Double) extends InterpolatedEvent
}
