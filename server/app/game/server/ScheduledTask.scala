package game.server

/**
  * A scheduled task that will be executed by the [[BasicGame]] at a later time.
  *
  * @param time   the scheduled time of execution
  * @param action the action that will be executed
  */
case class ScheduledTask (time: Double, action: () => Unit) {
	/** Whether this task was canceled */
	var canceled = false

	/**
	  * Cancels the task, preventing its action from being executed.
	  * Does nothing if the action was already executed when this method is called.
	  */
	def cancel(): Unit = canceled = true
}

object ScheduledTask {
	/** Ordering of [[ScheduledTask]] based on their scheduled time of execution */
	implicit val ordering: Ordering[ScheduledTask] = Ordering.by(_.time)
}
