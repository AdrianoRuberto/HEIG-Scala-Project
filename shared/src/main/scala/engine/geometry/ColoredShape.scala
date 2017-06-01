package engine.geometry

import scala.language.implicitConversions

case class ColoredShape (shape: Shape, color: String)

object ColoredShape {
	case class DefaultColor(color: String) extends AnyVal

	implicit def fromShape(s: Shape)(implicit c: DefaultColor): ColoredShape = ColoredShape(s, c.color)
	implicit def toShape(cs: ColoredShape): Shape = cs.shape
}
