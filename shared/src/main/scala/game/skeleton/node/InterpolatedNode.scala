package game.skeleton.node

import game.skeleton.AbstractSkeleton

class InterpolatedNode (private var targetValue: Double)
                       (implicit skeleton: AbstractSkeleton) extends Node[NodeEvent.InterpolatedEvent] {

	/** The current value of this node */
	private var currentValue: Double = targetValue

	/** Last time the current value was updated */
	private var lastTime: Double = 0.0

	/** End time of the interpolation */
	private var endTime: Double = 0.0

	/** Retrieves current time as floating point milliseconds */
	@inline private def currentTime: Double = System.nanoTime() / 1000000.0

	/**
	  * The current value of this interpolated node.
	  * This method will check the current time and perform interpolation of the value
	  * if the value of the node should still be in flux.
	  */
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

	/** The target value of this node */
	def value: Double = targetValue

	/**
	  * Begins node value interpolation.
	  *
	  * @param value    the target value of the interpolation
	  * @param duration the duration of the interpolation
	  */
	def interpolate(value: Double, duration: Double): Unit = if (targetValue != value) {
		targetValue = value
		if (duration <= 0.0) {
			currentValue = value
		} else {
			val now = currentTime
			lastTime = now
			endTime = now + duration
		}

		// Update with receiver latency awareness
		if (shouldSend) {
			if (duration <= 0.0) {
				this send NodeEvent.InterpolatedUpdate(value, duration)
			} else {
				this sendLatencyAware (latency => NodeEvent.InterpolatedUpdate(value, duration - latency))
			}
		}
	}

	/** Instantly updates this node value */
	def value_= (value: Double): Unit = interpolate(value, 0.0)

	/** Interpolates this node value at the given speed */
	def interpolateAtSpeed(value: Double, speed: Double): Unit = {
		if (speed == 0) stop()
		else interpolate(value, 1000.0 * (value - current) / speed)
	}

	/** Interrupts the interpolation */
	def stop(): Unit = interpolate(currentValue, 0)

	/** Receives a event from the server-side instance of this node */
	def receive(event: NodeEvent.InterpolatedEvent): Unit = event match {
		case NodeEvent.InterpolatedUpdate(t, d) => interpolate(t, d)
	}
}

object InterpolatedNode {
	def apply(value: Double)(implicit skeleton: AbstractSkeleton): InterpolatedNode = new InterpolatedNode(value)
}
