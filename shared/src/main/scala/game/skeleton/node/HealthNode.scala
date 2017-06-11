package game.skeleton.node

import game.skeleton.AbstractSkeleton

class HealthNode (maxValue: Double, regenRate: Double = 0.0)
                 (implicit skeleton: AbstractSkeleton) extends ResourceNode(maxValue, regenRate) {

	override def energize(amount: Double): Unit = if (res.value != 0) super.energize(amount)
	override def setupInterpolation(): Unit = if (res.value != 0) super.setupInterpolation()
}

object HealthNode {
	def apply(maxValue: Double, regenRate: Double = 0.0)(implicit skeleton: AbstractSkeleton): HealthNode = {
		new HealthNode(maxValue, regenRate)
	}
}
