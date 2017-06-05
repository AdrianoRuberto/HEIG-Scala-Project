package game.doodads

import engine.CanvasCtx
import engine.geometry._

package object area {
	private[area] def drawShape(ctx: CanvasCtx, shape: Shape): Unit = {
		shape match {
			case Circle(x, y, radius) =>
				ctx.arc(radius, radius, radius, 0, 2 * Math.PI)

			case Rectangle(x, y, width, height) =>
				ctx.rect(0, 0, width, height)

			case t @ Triangle(ax, ay, bx, by, cx, cy) =>
				ctx.moveTo(ax - t.boundingBox.x, ay - t.boundingBox.y)
				ctx.lineTo(bx - t.boundingBox.x, by - t.boundingBox.y)
				ctx.lineTo(cx - t.boundingBox.x, cy - t.boundingBox.y)

			case s @ Segment(x1, y1, x2, y2) =>
				ctx.moveTo(x1 - s.boundingBox.x, y1 - s.boundingBox.y)
				ctx.lineTo(x2 - s.boundingBox.x, y2 - s.boundingBox.y)
		}
	}
}
