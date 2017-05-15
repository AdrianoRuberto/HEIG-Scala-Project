package ctf

import org.scalajs.dom
import org.scalajs.dom.html

case class Canvas(id: String, width: Int, height: Int) {
	val element: html.Canvas = dom.document.createElement("canvas").asInstanceOf[html.Canvas]

	element.id = id
	element.setAttribute("width", s"${width}px")
	element.setAttribute("height", s"${height}px")
	dom.document.body.appendChild(element)

	val ctx: CanvasCtx = element.getContext("2d").asInstanceOf[CanvasCtx]
}
