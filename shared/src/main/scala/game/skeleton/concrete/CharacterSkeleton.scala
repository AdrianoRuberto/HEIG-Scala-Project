package game.skeleton.concrete

import engine.geometry.Vector2D
import game.UID
import game.skeleton.node.{InterpolatedNode, ResourceNode, SimpleNode}
import game.skeleton.{Skeleton, _}

class CharacterSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Character, uid, remotes) {

	val name = SimpleNode("Unknown")
	val color = SimpleNode("#999")

	val moving = SimpleNode(false)
	val x = InterpolatedNode(0.0)
	val y = InterpolatedNode(0.0)
	val speed = InterpolatedNode(150.0)

	def position: Vector2D = Vector2D(x.current, y.current)

	val facingOverride = SimpleNode(false)
	val facingDirection = SimpleNode(0.0)

	val health = ResourceNode(200)
	val energy = ResourceNode(100, 15)
	val dead = SimpleNode(false)
}
