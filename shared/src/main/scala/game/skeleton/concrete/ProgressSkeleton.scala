package game.skeleton.concrete

import game.UID
import game.skeleton.node.{InterpolatedNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManager, SkeletonType}

/**
  * A generic Skeleton encoding a progress bar
  */
class ProgressSkeleton (uid: UID, remotes: Seq[RemoteManager])
	extends AbstractSkeleton(SkeletonType.Progress, remotes, uid) {

	val from = SimpleNode(0.0)
	val to = SimpleNode(1.0)
	val progress = InterpolatedNode(0.0)

	def begin(from: Double, to: Double, over: Double): Unit = {
		this.from.value = from
		this.to.value = to
		this.progress.value = from
		this.progress.interpolate(to, over)
	}

	def value: Double = progress.current
	def percent: Double = (value - from.value) / to.value
}
