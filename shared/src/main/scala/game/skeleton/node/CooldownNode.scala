package game.skeleton.node

import game.skeleton.AbstractSkeleton

case class CooldownNode (implicit skeleton: AbstractSkeleton) {
	private val cd = InterpolatedNode(1.0)

	/** Whether this spell is ready of not */
	def ready: Boolean = cd.current == 1.0

	/** Cooldown progress as percent */
	def progress: Double = cd.current

	/** Start the cooldown for the given duration */
	def start(duration: Double): Unit = {
		if (!ready) throw new IllegalStateException("Cannot start cooldown while already cooling down")
		cd.value = 0.0
		cd.interpolate(1.0, duration)
	}

	def reset(): Unit = {
		cd.value = 1
	}
}
