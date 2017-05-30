package game.skeleton.concrete

import boopickle.DefaultBasic._
import game.UID
import game.skeleton.{AbstractSkeleton, SimpleNode, Transmitter, Type}

class CharacterSkeleton (uid: UID = UID.next)
                        (implicit receiver: Transmitter = Transmitter.NoTransmitter)
		extends AbstractSkeleton(Type.Character, uid) {

	val name = SimpleNode("Unknown")
	val x = SimpleNode(0.0)
	val y = SimpleNode(0.0)
}
