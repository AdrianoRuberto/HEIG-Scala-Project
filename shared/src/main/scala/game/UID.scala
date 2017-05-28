package game

import boopickle.DefaultBasic._

case class UID private (value: Int) extends AnyVal

object UID {
	implicit val pickler: Pickler[UID] = transformPickler(UID.apply)(_.value)

	private var lastUID = 0
	def next: UID = {
		lastUID += 1
		UID(lastUID)
	}
}
