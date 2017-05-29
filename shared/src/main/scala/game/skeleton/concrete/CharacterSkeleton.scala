package game.skeleton.concrete

import boopickle.DefaultBasic._
import game.UID
import game.skeleton.{AbstractSkeleton, Node, Transmitter, Type}

class CharacterSkeleton (uid: UID = UID.next)
                        (implicit receiver: Transmitter = Transmitter.NoTransmitter)
		extends AbstractSkeleton(Type.Character, uid) {

	val name = Node("Unknown")
	val x = Node(0.0)
	val y = Node(0.0)
}
