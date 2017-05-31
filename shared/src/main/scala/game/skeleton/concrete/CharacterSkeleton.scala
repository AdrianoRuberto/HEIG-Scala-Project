package game.skeleton.concrete

import boopickle.DefaultBasic._
import game.UID
import game.skeleton._
import game.skeleton.node.{InterpolatedNode, SimpleNode}

class CharacterSkeleton (uid: UID = UID.next)
                        (implicit receiver: Transmitter = Transmitter.NoTransmitter)
		extends AbstractSkeleton(Type.Character, uid) {

	val name = SimpleNode("Unknown")
	val x = InterpolatedNode(0.0)
	val y = InterpolatedNode(0.0)
	val speed = SimpleNode(100)
	val color = SimpleNode("black")
}
