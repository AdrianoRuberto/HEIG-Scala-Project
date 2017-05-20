package engine
package entity
package generics

trait GenericMouseHandler extends Entity with feature.MouseEvents {
	def handleMouse(tpe: String, x: Double, y: Double, button: Int): Unit = ()
}
