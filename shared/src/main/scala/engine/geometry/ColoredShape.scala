package engine.geometry

import macros.pickle
import scala.language.implicitConversions

@pickle case class ColoredShape (shape: Shape, color: String)

object ColoredShape {
	case class DefaultColor(color: String) extends AnyVal

	implicit def fromShape(s: Shape)(implicit c: DefaultColor): ColoredShape = ColoredShape(s, c.color)
	implicit def toShape(cs: ColoredShape): Shape = cs.shape
}
