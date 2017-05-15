package ctf
package actors

import ctf.utils.{Layer, MouseSupport, Point}

class MouseDebug(x: Double, y: Double) extends Actor(Layer.Interface) with MouseSupport {
	val position: Point = Point(x, y)

	def draw(ctx: CanvasCtx): Unit = {
		ctx.fillText(mousePosition.toString.drop(5), 0, 0)
	}
}
