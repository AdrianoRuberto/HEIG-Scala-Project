package utils

import game.protocol.ServerMessage
import game.protocol.ServerMessage.Severity
import play.api.libs.json._
import scala.language.implicitConversions

object Debug {
	class Serializable(private val value: JsValue)
	object Serializable {
		implicit def wrap[T](field: T)(implicit w: Writes[T]): Serializable = new Serializable(w.writes(field))
		implicit def wrap(field: Throwable): Serializable = new Serializable(JsString(writeThrowable(field)))

		/** Encodes an error as JSON */
		private def writeThrowable(throwable: Throwable): String = {
			if (throwable == null) "null"
			else {
				val ss = StringBuilder.newBuilder
				ss.append(throwable.toString)
				for (frame <- throwable.getStackTrace) {
					ss.append("\n\tat ").append(frame.toString)
				}
				val cause = throwable.getCause
				if (cause != null) {
					ss.append("\nCaused by: ")
					ss.append(writeThrowable(cause))
				}
				ss.toString()
			}
		}

		def extract(s: Serializable): JsValue = s.value
	}

	def log(args: Serializable*): ServerMessage.Debug = ServerMessage.Debug(Severity.Log, args.map(stringify))
	def warn(args: Serializable*): ServerMessage.Debug = ServerMessage.Debug(Severity.Warn, args.map(stringify))
	def error(args: Serializable*): ServerMessage.Debug = ServerMessage.Debug(Severity.Error, args.map(stringify))

	private def stringify(s: Serializable): String = Serializable.extract(s).toString()
}
