package game.skeleton.core

import engine.geometry.Vector2D
import game.UID
import game.skeleton.node.{HealthNode, InterpolatedNode, ResourceNode, SimpleNode}
import game.skeleton.{AbstractSkeleton, RemoteManagerAgent, Skeleton}
import utils.Color

case class CharacterSkeleton (uid: UID, remotes: Iterable[RemoteManagerAgent])
	extends AbstractSkeleton(Skeleton.Character) {

	val name = SimpleNode("Unknown")
	val color = SimpleNode(Color("#999"))

	val moving = SimpleNode(false)
	val x = InterpolatedNode(0.0)
	val y = InterpolatedNode(0.0)
	val speed = InterpolatedNode(150.0)

	def position: Vector2D = Vector2D(x.current, y.current)

	val facingOverride = SimpleNode(false)
	val facingDirection = SimpleNode(0.0)

	val health = HealthNode(200)
	val energy = ResourceNode(100, 15)
	val dead = SimpleNode(false)
}
