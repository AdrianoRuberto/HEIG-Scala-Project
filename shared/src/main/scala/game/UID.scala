package game

import boopickle.DefaultBasic._
import java.util.concurrent.atomic.AtomicInteger

final case class UID private (value: Int) extends AnyVal {
	@inline def zero: Boolean = value == 0
}

object UID {
	private val lastUID = new AtomicInteger(0)
	def next: UID = {
		val id = lastUID.incrementAndGet()
		if (id == 0) next
		else UID(id)
	}

	val zero = UID(0)

	implicit val UIDPickler: Pickler[UID] = transformPickler(UID.apply)(_.value)
}
