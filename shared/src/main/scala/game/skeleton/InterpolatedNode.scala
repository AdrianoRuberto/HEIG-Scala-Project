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
			if (now != lastTime) {
				if (now >= endTime) {
					currentValue = targetValue
				} else {
					val delta = targetValue - currentValue
					val timeSpan = now - lastTime
					val timeLeft = endTime - lastTime
					currentValue += delta * (timeSpan / timeLeft)
				}
				lastTime = now
			}
		}
		currentValue
	}

	def value: Double = targetValue

	def interpolate(value: Double, duration: Double): Unit = {
		targetValue = value
		if (duration <= 0.0) {
			currentValue = value
		} else {
			val now = currentTime
			lastTime = now
			endTime = now + duration
		}

		// Update with receiver latency awareness
		if (shouldEmit) {
			if (duration <= 0.0) {
				this emit InterpolatedUpdate(value, duration)
			} else {
				this emitLatencyAware (latency => Event.InterpolatedUpdate(value, duration - latency))
			}
		}
	}

	def value_= (value: Double): Unit = interpolate(value, 0.0)

	def interpolateAtSpeed(value: Double, speed: Double): Unit = {
		if (speed == 0) stop()
		else interpolate(value, 1000.0 * (value - currentValue) / speed)
	}

	def stop(): Unit = interpolate(targetValue, 0)

	/** Receives a event from the server-side instance of this node */
	def receive(event: Event.InterpolatedNodeEvent): Unit = event match {
		case Event.InterpolatedUpdate(t, d) => interpolate(t, d)
	}
}
