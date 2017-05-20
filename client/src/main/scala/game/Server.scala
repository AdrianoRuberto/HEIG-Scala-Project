package game

import boopickle.DefaultBasic._
import java.nio.ByteBuffer
import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import scala.scalajs.js
import scala.scalajs.js.typedarray._

object Server {
	private var socket: dom.WebSocket = _

	def searchGame(name: String, fast: Boolean): Unit = {
		require(socket == null, "Attempted to search for game while socket is still open")
		socket = new WebSocket(s"ws://${dom.document.location.host}/socket")
		socket.binaryType = "arraybuffer"
		socket.on(Event.Open) { _ => Server ! ClientMessage.SearchGame(name, fast) }
		socket.on(Event.Close)(socketClosed)
		socket.on(Event.Error)(socketClosed)
		socket.on(Event.Message) { msg =>
			val buffer = TypedArrayBuffer.wrap(msg.data.asInstanceOf[ArrayBuffer])
			handleMessage(Unpickle[ServerMessage].fromBytes(buffer))
		}
	}

	def disconnect(): Unit = {
		require(socket != null, "Attempted to disconnect from server while not connected")
		socket.close()
		socket = null
	}

	def ! (msg: ClientMessage): Unit = {
		//require(socket != null, "Attempted to send message to server while not connected")
		val buffer = Pickle.intoBytes(msg).toArrayBuffer
		socket.send(buffer)
	}

	def socketClosed(e: dom.Event): Unit = {
		socket = null
	}

	def handleMessage(msg: ServerMessage): Unit = msg match {
		case ServerMessage.Error(e) => dom.console.error(e)
		case ServerMessage.JsonError(e) => dom.console.error(js.JSON.parse(e))
		case ServerMessage.ServerError => App.reboot()
		case lm: ServerMessage.LobbyMessage => Lobby.message(lm)
	}

	implicit class ByteBufferOps(private val buffer: ByteBuffer) extends AnyVal {
		def toArrayBuffer: ArrayBuffer = {
			val length = buffer.remaining()
			TypedArrayBufferOps.byteBufferOps(buffer).arrayBuffer().slice(0, length)
		}
	}
}
