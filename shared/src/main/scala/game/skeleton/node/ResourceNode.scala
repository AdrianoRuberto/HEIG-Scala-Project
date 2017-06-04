package game.skeleton.node

import game.skeleton.AbstractSkeleton

case class ResourceNode (private var maxValue: Double, private var regenRate: Double = 0.0)
                        (implicit skeleton: AbstractSkeleton) {
	private val res = InterpolatedNode(maxValue)

	def max: Double = maxValue
	def current: Double = res.current
	def percent: Double = current / max

	def rate: Double = regenRate

	def rate_= (newRate: Double): Unit = {
		regenRate = newRate
		setupInterpolation()
	}

	def consume(amount: Double): Unit = {
		val updated = (res.value - amount) min maxValue max 0.0
		if (rate != 0) {
			res.value = updated
			setupInterpolation()
		} else {
			res.interpolate(updated, 200)
		}
	}

	def energize(amount: Double): Unit = consume(-amount)

	@inline final def += (amount: Double): Unit = energize(amount)
	@inline final def -= (amount: Double): Unit = consume(amount)

	private def setupInterpolation(): Unit = {
		if (regenRate > 0) res.interpolateAtSpeed(maxValue, regenRate)
		else if (regenRate < 0) res.interpolateAtSpeed(0.0, regenRate)
	}
}
