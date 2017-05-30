package game.skeleton

import boopickle.DefaultBasic._
import game.skeleton.Event.{SimpleNodeEvent, SimpleUpdate}

case class SimpleNode[T: Pickler] private (private var current: T)
                                     (implicit skeleton: AbstractSkeleton) extends Node[SimpleNodeEvent] {

	def value: T = current

	def value_=(newValue: T): Unit = {
		current = newValue
		this emit Event.SimpleUpdate(pickle(newValue))
	}

	override def receive(event: SimpleNodeEvent): Unit = event match {
		case SimpleUpdate(buffer) => value = unpickle(buffer)
	}
}
