package game.skeleton.concrete

import boopickle.DefaultBasic._
import engine.geometry.Vector2D
import game.UID
import game.protocol.enums.SkeletonType
import game.skeleton._
import game.skeleton.node.{InterpolatedNode, ResourceNode, SimpleNode}

class CharacterSkeleton (uid: UID, remotes: Seq[RemoteManager])
	extends AbstractSkeleton(SkeletonType.Character, remotes, uid) {

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
}
