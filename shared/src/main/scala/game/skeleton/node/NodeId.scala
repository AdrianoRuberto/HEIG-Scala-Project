package game.skeleton.node

import boopickle.DefaultBasic._

/**
  * Node IDs are used to identify nodes inside a skeleton.
  *
  * Skeletons are forbidden to dynamically create nodes, and must creates every
  * nodes instance inside their constructor. This way, both the server-side and
  * the client-side version of the skeleton will hold the same nodes with the
  * same numbering.
  *
  * @param value the value of this ID
  */
case class NodeId(value: Int) extends AnyVal

object NodeId {
	implicit val pickler: Pickler[NodeId] = transformPickler(NodeId.apply)(_.value)
}
