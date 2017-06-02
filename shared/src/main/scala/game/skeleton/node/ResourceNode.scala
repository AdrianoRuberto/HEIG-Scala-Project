package game.skeleton.node

import game.skeleton.AbstractSkeleton

class ResourceNode (private var maxValue: Double, private var regenRate: Double = 0.0)
                   (implicit skeleton: AbstractSkeleton) extends InterpolatedNode(maxValue) {

	def rate: Double = regenRate

	def rate_= (newRate: Double): Unit = {
		regenRate = newRate
		setupInterpolation()
	}

	def max: Double = maxValue

	def percent: Double = current / maxValue

	def consume(amount: Double): Boolean = {
		if (amount > current) false
		else {
			value = (value - amount) min maxValue
			setupInterpolation()
			true
		}
	}

	def energize(amount: Double): Unit = consume(-amount)

	private def setupInterpolation(): Unit = {
		if (regenRate > 0) interpolateAtSpeed(maxValue, regenRate)
		else if (regenRate < 0) interpolateAtSpeed(0.0, regenRate)
	}
}

object ResourceNode {
	def apply(maxValue: Double, regenRate: Double = 0.0)(implicit skeleton: AbstractSkeleton): ResourceNode = {
		new ResourceNode(maxValue, regenRate)
	}
}
