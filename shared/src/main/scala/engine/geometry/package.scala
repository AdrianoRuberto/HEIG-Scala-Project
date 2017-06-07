package engine

import boopickle.DefaultBasic.{Pickler, compositePickler}

package object geometry {
	private[geometry] object g {
		// http://www.sevenson.com.au/actionscript/sat/
		def intersect(a: ConvexPolygon, b: ConvexPolygon): Boolean = {
			val axes = a.axes ++ b.axes
			!axes.exists { axis =>
				val ap = a.vertices.map(_ scalarProject axis)
				val bp = b.vertices.map(_ scalarProject axis)
				ap.max >= bp.min && bp.max >= ap.min
			}
		}

		// http://www.sevenson.com.au/actionscript/sat/
		def intersect(cp: ConvexPolygon, c: Circle): Boolean = {
			val nearest = cp.vertices.minBy(_ <-> c.center)
			val circleAxis = (c.center - nearest).normalized
			val axes = cp.axes + circleAxis
			!axes.exists { axis =>
				val a = cp.vertices.map(_ scalarProject axis)
				val b = c.center scalarProject axis
				a.max >= b - c.radius && b + c.radius >= a.min
			}
		}
	}

	implicit val ShapePickler: Pickler[Shape] = compositePickler[Shape]
		.addConcreteType[Segment]
		.addConcreteType[Triangle]
		.addConcreteType[Rectangle]
		.addConcreteType[Circle]
}
