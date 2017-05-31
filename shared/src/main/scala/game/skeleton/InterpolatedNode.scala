package game.skeleton

import game.skeleton.Event.InterpolatedUpdate

case class InterpolatedNode (private var targetValue: Double)
                            (implicit skeleton: AbstractSkeleton) extends Node[Event.InterpolatedNodeEvent] {

	private var currentValue: Double = targetValue
	private var lastTime: Double = 0.0
	private var endTime: Double = 0.0

	@inline private def currentTime: Double = System.nanoTime() / 1000000.0

	def current: Double = {
		if (currentValue != targetValue) {
			val now = currentTime
			if (now >= endTime) {
				currentValue = targetValue
			} else if (now != lastTime) {
				val delta = targetValue - currentValue
				val timeSpan = now - lastTime
				val timeLeft = endTime - lastTime
				currentValue += delta * (timeSpan / timeLeft)
				lastTime = now
			}
		}
		currentValue
	}

	def value: Double = targetValue

	def value_= (value: Double, duration: Double = 0): Unit = {
		targetValue = value
		if (duration == 0) {
			currentValue = value
		} else {
			endTime = currentTime + duration
		}

		// Update with receiver latency awareness
		if (shouldEmit) {
			if (duration == 0) {
				this emit InterpolatedUpdate(value, duration)
			} else {
				this emitLatencyAware (latency => Event.InterpolatedUpdate(value, duration - latency))
			}
		}
	}

	/** Allow for `.value = (value, duration)` syntax */
	def value_= (valueAndDuration: (Double, Double)): Unit = value_=(valueAndDuration._1, valueAndDuration._2)

	/** Receives a event from the server-side instance of this node */
	def receive(event: Event.InterpolatedNodeEvent): Unit = event match {
		case Event.InterpolatedUpdate(t, d) => value = (t, d)
	}
}
