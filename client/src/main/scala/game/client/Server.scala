package game.client

import boopickle.DefaultBasic._
import game.protocol.ServerMessage.Severity
import game.protocol.{ClientMessage, ServerMessage}
import java.nio.ByteBuffer
import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import scala.scalajs.js
import scala.scalajs.js.typedarray._
import scala.util.Try

object Server {
	private var socket: dom.WebSocket = null

	var latency: Double = 0

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

	def disconnect(silent: Boolean = false): Unit = {
		require(silent || socket != null, "Attempted to disconnect from server while not connected")
		if (socket != null) {
			socket.close()
			socket = null
		}
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
		case ServerMessage.Bundle(messages) => messages.foreach(handleMessage)
		case ServerMessage.Ping(ms, payload) =>
			latency = ms
			this ! ClientMessage.Ping(payload)
		case ServerMessage.Debug(severity, args) => debugOutput(severity, args)
		case ServerMessage.ServerError => App.reboot(true)
		case ServerMessage.GameEnd => App.reboot()
		case lm: ServerMessage.LobbyMessage => Lobby.message(lm)
		case gm: ServerMessage.GameMessage => println(gm); Game.message(gm)
	}

	private def debugOutput(severity: Severity, args: Seq[String]): Unit = if (args.nonEmpty) {
		val parsed = args.map { arg =>
			Try(js.JSON.parse(arg).asInstanceOf[js.Any]).getOrElse(arg.asInstanceOf[js.Any])
		}
		val console = dom.console.asInstanceOf[js.Dynamic]
		val handler = severity match {
			case ServerMessage.Severity.Verbose => console.debug
			case ServerMessage.Severity.Info => console.log
			case ServerMessage.Severity.Warn => console.warn
			case ServerMessage.Severity.Error => console.error
		}
		for (f <- handler.asInstanceOf[js.UndefOr[js.Function]]) {
			f.call(console, parsed: _*)
		}
	}

	implicit class ByteBufferOps(private val buffer: ByteBuffer) extends AnyVal {
		def toArrayBuffer: ArrayBuffer = {
			val length = buffer.remaining()
			TypedArrayBufferOps.byteBufferOps(buffer).arrayBuffer().slice(0, length)
		}
	}
}
