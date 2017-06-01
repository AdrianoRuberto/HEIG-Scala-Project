package game.skeleton.concrete

import boopickle.DefaultBasic._
import game.UID
import game.protocol.enums.SkeletonType
import game.skeleton._
import game.skeleton.node.{InterpolatedNode, ResourceNode, SimpleNode}

class CharacterSkeleton (uid: UID = UID.next)
                        (implicit receiver: Transmitter = Transmitter.NoTransmitter)
		extends AbstractSkeleton(SkeletonType.Character, uid) {

	val name = SimpleNode("Unknown")
	val color = SimpleNode("#999")

	val moving = SimpleNode(false)
	val x = InterpolatedNode(0.0)
	val y = InterpolatedNode(0.0)
	val speed = SimpleNode(150)

	val facingOverride = SimpleNode(false)
	val facingDirection = SimpleNode(0.0)

	val health = ResourceNode(200)
	val energy = ResourceNode(100, 15)
}
