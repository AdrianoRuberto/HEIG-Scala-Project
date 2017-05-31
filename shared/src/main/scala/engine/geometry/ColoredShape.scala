package engine.geometry

import scala.language.implicitConversions

case class ColoredShape(shape: Shape, color: String = "#FF63e5")

object ColoredShape {
	implicit def toShape(cs: ColoredShape): Shape = cs.shape
}
