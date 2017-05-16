package engine
package actor

abstract class Actor {
	private[engine] def registerWith(engine: Engine): Unit = ()
	private[engine] def unregisterFrom(engine: Engine): Unit = ()
}
