package game.entities

class Resource (var value: Double,
                var max: Double,
                var regen: Double = 0,
                var smoothing: Boolean = false) {

	var smoothValue: Double = value
	var drain: Double = 0

	@inline final def percent: Double = value / max
	@inline final def smoothPercent: Double = smoothValue / max

	def += (amount: Double): Unit = value = clamp(value + amount)
	@inline final def -= (amount: Double): Unit = this += -amount

	def startDrain(rate: Double): Unit = drain += rate
	def stopDrain(rate: Double): Unit = drain -= rate

	@inline private final def clamp(value: Double) = Math.max(0.0, Math.min(max, value))

	def update(dt: Double): Unit = {
		value = clamp(value + (regen - drain) * dt / 1000)

		// Smoothing
		if (smoothing) {
			val delta = value - smoothValue
			smoothValue = if (Math.abs(delta) < 0.1) value else smoothValue + delta / 3
		}
	}
}
