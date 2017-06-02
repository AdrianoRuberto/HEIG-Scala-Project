package game.protocol

import boopickle.Default._
import engine.geometry.{ColoredShape, Shape}
import game.protocol.enums.GameMode
import game.skeleton.ManagerEvent
import game.{TeamInfo, UID}

sealed trait ServerMessage

object ServerMessage {
	// Core messages
	case object ServerError extends ServerMessage
	case object GameEnd extends ServerMessage
	case class Ping(latency: Double, payload: Long) extends ServerMessage with SystemMessage
	case class Bundle(messages: Seq[ServerMessage]) extends ServerMessage with SystemMessage

	// Lobby messages
	sealed trait LobbyMessage extends ServerMessage
	case class QueueUpdate(count: Int) extends LobbyMessage
	case class GameFound(mode: GameMode, team: Seq[TeamInfo], me: UID, warmup: Int) extends LobbyMessage

	// Game messages
	sealed trait GameMessage extends ServerMessage
	case object GameStart extends GameMessage
	case class SkeletonEvent(event: ManagerEvent) extends GameMessage
	case class InstantiateCharacter(characterUID: UID, skeletonUID: UID) extends GameMessage
	case class DrawShape(shapeUID: UID, shape: ColoredShape) extends GameMessage
	case class EraseShape(shapeUID: UID) extends GameMessage

	case class GainSpell(slot: Int, skeletonUID: UID) extends GameMessage
	case class LoseSpell(slot: Int) extends GameMessage

	// Camera
	case class SetCameraLocation(x: Double, y: Double) extends GameMessage
	case class SetCameraFollow(characterUID: UID) extends GameMessage
	case class SetCameraSmoothing(smoothing: Boolean) extends GameMessage
	case class SetCameraSpeed(pps: Double) extends GameMessage

	// Debug message
	sealed trait Severity
	object Severity {
		case object Verbose extends Severity
		case object Info extends Severity
		case object Warn extends Severity
		case object Error extends Severity
		implicit val pickler: Pickler[Severity] = generatePickler[Severity]
	}
	case class Debug(severity: Severity, args: Seq[String]) extends ServerMessage with SystemMessage

	private implicit val UIDPicker = UID.pickler
	private implicit val SkeletonPickler = ManagerEvent.pickler
	private implicit val ShapePickler =  Shape.pickler
	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
