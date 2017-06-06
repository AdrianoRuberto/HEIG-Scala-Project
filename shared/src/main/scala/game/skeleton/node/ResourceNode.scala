package game.skeleton.node

import game.skeleton.AbstractSkeleton

case class ResourceNode (private var maxValue: Double, private var regenRate: Double = 0.0)
                        (implicit skeleton: AbstractSkeleton) {
	private val res = InterpolatedNode(maxValue)
	private var rateInterpolated = false

	def current: Double = res.current
	def percent: Double = current / max

	def max: Double = maxValue

	def max_= (newMax: Double): Unit = {
		val base = if (rateInterpolated) res.current else res.value
		maxValue = newMax
		res.value = base / max * newMax
		setupInterpolation()
	}

	def rate: Double = regenRate

	def rate_= (newRate: Double): Unit = {
		regenRate = newRate
		setupInterpolation()
	}

	def consume(amount: Double): Unit = {
		val base = if (rateInterpolated) res.current else res.value
		val updated = (base - amount) min maxValue max 0.0
		if (rate != 0) {
			res.value = updated
			setupInterpolation()
		} else {
			res.interpolate(updated, 200)
			rateInterpolated = false
		}
	}

	def energize(amount: Double): Unit = consume(-amount)

	@inline final def += (amount: Double): Unit = energize(amount)
	@inline final def -= (amount: Double): Unit = consume(amount)

	private def setupInterpolation(): Unit = {
		if (regenRate > 0) {
			res.interpolateAtSpeed(maxValue, regenRate)
			rateInterpolated = true
		} else if (regenRate < 0) {
			res.interpolateAtSpeed(0.0, regenRate)
			rateInterpolated = true
		} else {
			res.stop()
			rateInterpolated = false
		}
	}
}
