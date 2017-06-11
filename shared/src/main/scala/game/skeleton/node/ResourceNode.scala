package game.skeleton.node

import game.skeleton.AbstractSkeleton

class ResourceNode (private var initialMax: Double, private var initialRegen: Double = 0.0)
                   (implicit skeleton: AbstractSkeleton) {

	private val maxAmount = SimpleNode(initialMax)
	private val regenRate = SimpleNode(initialRegen)
	private val currentAmount = InterpolatedNode(initialMax)
	private var rateInterpolated = false

	def max: Double = maxAmount.value
	def max_= (newMax: Double): Unit = {
		val ratio = value / max
		maxAmount.value = newMax
		currentAmount.value = ratio * max
		setupInterpolation()
	}

	def value: Double = if (rateInterpolated) current else currentAmount.value
	def value_= (newValue: Double): Unit = {
		currentAmount.value = newValue
		setupInterpolation()
	}

	def current: Double = currentAmount.current
	def percent: Double = current / max

	def rate: Double = regenRate.value
	def rate_= (rate: Double): Unit = {
		regenRate.value = rate
		setupInterpolation()
	}

	def consume(amount: Double): Unit = {
		val updated = 0.0 max (value - amount) min maxAmount.value
		if (rate != 0) {
			currentAmount.value = updated
			setupInterpolation()
		} else {
			currentAmount.interpolate(updated, 200)
			rateInterpolated = false
		}
	}

	def energize(amount: Double): Unit = consume(-amount)

	@inline final def += (amount: Double): Unit = energize(amount)
	@inline final def -= (amount: Double): Unit = consume(amount)

	private def setupInterpolation(): Unit = {
		if (rate > 0) {
			currentAmount.interpolateAtSpeed(max, rate)
			rateInterpolated = true
		} else if (rate < 0) {
			currentAmount.interpolateAtSpeed(0.0, rate)
			rateInterpolated = true
		} else {
			currentAmount.stop()
			rateInterpolated = false
		}
	}
}

object ResourceNode {
	def apply(initialMax: Double, initialRegen: Double = 0.0)(implicit skeleton: AbstractSkeleton): ResourceNode = {
		new ResourceNode(initialMax, initialRegen)
	}
}
