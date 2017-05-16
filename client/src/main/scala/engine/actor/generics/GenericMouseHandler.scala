package engine
package actor
package generics

trait GenericMouseHandler extends Actor with feature.MouseEvents {
	def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = ()
}
