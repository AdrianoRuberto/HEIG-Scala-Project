package game

import boopickle.DefaultBasic._
import java.nio.ByteBuffer
import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer, TypedArrayBufferOps}

object Server {
	private var socket: dom.WebSocket = _

	def searchGame(player: Player): Unit = {
		socket = new WebSocket(s"ws://${dom.document.location.host}/socket")
		socket.binaryType = "arraybuffer"
		socket.on(Event.Open) { _ => Server ! ClientMessage.SearchGame(player) }
		socket.on(Event.Close)(socketClosed)
		socket.on(Event.Error)(socketClosed)
		socket.on(Event.Message) { msg =>
			val buffer = TypedArrayBuffer.wrap(msg.data.asInstanceOf[ArrayBuffer])
			handleMessage(Unpickle[ServerMessage].fromBytes(buffer))
		}
	}

	def ! (msg: ClientMessage): Unit = {
		val buffer = Pickle.intoBytes(msg).toArrayBuffer
		socket.send(buffer)
	}

	def socketClosed(e: dom.Event): Unit = ()

	def handleMessage(msg: ServerMessage): Unit = msg match {
		case ServerMessage.Error(e) => dom.console.error(e)
		case ServerMessage.GameFound(_) => println("Game found !")
	}

	implicit class ByteBufferOps(private val buffer: ByteBuffer) extends AnyVal {
		def toArrayBuffer: ArrayBuffer = TypedArrayBufferOps.byteBufferOps(buffer).arrayBuffer()
	}
}
