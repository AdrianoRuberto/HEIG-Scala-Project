import org.scalajs.dom

package object game {
	implicit class EventTargetOps(private val eventTarget: dom.EventTarget) extends AnyVal {
		def on[T <: dom.Event](event: Event[T])(handler: T => Unit): Unit = {
			eventTarget.addEventListener(event.name, handler)
		}
	}

	abstract class Event[T <: dom.Event](val name: String)

	object Event {
		object TransitionEnd extends Event[dom.TransitionEvent]("transitionend")
		object Click extends Event[dom.MouseEvent]("click")
		object Input extends Event[dom.Event]("input")
		object KeyUp extends Event[dom.KeyboardEvent]("keyup")
		object Blur extends Event[dom.FocusEvent]("blur")
		object Load extends Event[dom.UIEvent]("load")
	}
}
