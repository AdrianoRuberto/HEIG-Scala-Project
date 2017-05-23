package engine.geometry

import boopickle.DefaultBasic._

case class Size(width: Double, height: Double)

object Size {
	implicit val pickler: Pickler[Size] = PicklerGenerator.generatePickler[Size]
}
