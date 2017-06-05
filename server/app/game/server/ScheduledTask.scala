package game.server

case class ScheduledTask (time: Double, action: () => Unit) {
	var canceled = false
	def cancel(): Unit = canceled = true
}

object ScheduledTask {
	implicit val ordering: Ordering[ScheduledTask] = Ordering.by(_.time)
}
