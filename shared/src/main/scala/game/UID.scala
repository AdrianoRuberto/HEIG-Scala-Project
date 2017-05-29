package game

import boopickle.DefaultBasic._
import java.util.concurrent.atomic.AtomicInteger

case class UID private (value: Int) extends AnyVal

object UID {
	implicit val pickler: Pickler[UID] = transformPickler(UID.apply)(_.value)

	private val lastUID = new AtomicInteger(0)
	def next: UID = UID(lastUID.incrementAndGet())
}
