package engine.utils

case class Layer(strata: Double) extends AnyVal {
	def / (sublayer: Int): Layer = {
		require(strata.isWhole, "Nested sub-layers are forbidden")
		require(sublayer >= 0 && sublayer < 1000, s"Invalid sub-layer index: $sublayer")
		Layer(strata + sublayer / 1000.0)
	}
}

object Layer {
	final val World = Layer(1)
	final val LowFx = Layer(3)
	final val Players = Layer(5)
	final val Nameplates = Layer(6)
	final val HighFx = Layer(8)
	final val Interface = Layer(10)

	implicit object LayerIsOrdered extends Ordering[Layer] {
		def compare(x: Layer, y: Layer): Int = x.strata compareTo y.strata
	}
}
