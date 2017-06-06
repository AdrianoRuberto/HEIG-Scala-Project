package utils

import boopickle.DefaultBasic._

final class Color private (val red: Int, val green: Int, val blue: Int, val alpha: Double) {
	def * (k: Double): Color = new Color(red, green, blue, alpha * k)
	override def toString: String = s"rgba($red, $green, $blue, $alpha)"

	def value: Int = {
		val a = ((alpha * 255 + 0.5).toInt & 0xFF) << 24
		val r = (red & 0xFF) << 16
		val g = (green & 0xFF) << 8
		val b = blue & 0xFF
		a | r | g | b
	}
}

object Color {
	def apply(red: Int, green: Int, blue: Int, alpha: Double = 1.0): Color = {
		new Color(0 max red min 255, 0 max green min 255, 0 max blue min 255, 0.0 max alpha min 1.0)
	}

	private val short = "#([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])".r
	private val long = "#([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})".r

	def apply(color: String): Color = color match {
		case short(r, g, b) =>
			Color(Integer.parseInt(r * 2, 16), Integer.parseInt(g * 2, 16), Integer.parseInt(b * 2, 16))
		case long(r, g, b) =>
			Color(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16))
		case _ =>
			throw new IllegalArgumentException(s"Unable to parse color string: $color")
	}

	def apply(value: Int): Color = {
		val a = ((value >> 24) & 0xFF) / 255.0
		val r = (value >> 16) & 0xFF
		val g = (value >> 8) & 0xFF
		val b = value & 0xFF
		Color(r, g, b, a)
	}

	implicit val pickler: Pickler[Color] = transformPickler((v: Int) => Color(v))(_.value)
}
