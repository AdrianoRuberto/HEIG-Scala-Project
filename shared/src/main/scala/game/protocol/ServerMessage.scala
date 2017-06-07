package game.protocol

import engine.geometry.ColoredShape
import game.doodads.Doodad
import game.skeleton.ManagerEvent
import game.{GameMode, TeamInfo, UID}
import macros.pickle

@pickle sealed trait ServerMessage

object ServerMessage {
	// Core messages
	@pickle case object ServerError extends ServerMessage
	@pickle case object GameEnd extends ServerMessage
	@pickle case class Ping(latency: Double, payload: Long) extends ServerMessage with SystemMessage
	@pickle case class Bundle(messages: Seq[ServerMessage]) extends ServerMessage with SystemMessage

	// Lobby messages
	sealed trait LobbyMessage extends ServerMessage
	@pickle case class QueueUpdate(count: Int) extends LobbyMessage
	@pickle case class GameFound(mode: GameMode, team: Seq[TeamInfo], me: UID, warmup: Int) extends LobbyMessage

	// Game messages
	sealed trait GameMessage extends ServerMessage
	@pickle case object GameStart extends GameMessage
	@pickle case class SkeletonEvent(event: ManagerEvent) extends GameMessage
	@pickle case object EnableInputs extends GameMessage
	@pickle case object DisableInputs extends GameMessage

	// Entities
	@pickle case class InstantiateCharacter(characterUID: UID, skeletonUID: UID) extends GameMessage
	@pickle case class DrawShape(shapeUID: UID, shape: ColoredShape) extends GameMessage
	@pickle case class EraseShape(shapeUID: UID) extends GameMessage
	@pickle case class GainSpell(slot: Int, skeletonUID: UID) extends GameMessage
	@pickle case class LoseSpell(slot: Int) extends GameMessage
	@pickle case class CreateDoodad(uid: UID, doodad: Doodad) extends GameMessage
	@pickle case class DestroyDoodad(uid: UID) extends GameMessage

	// Camera
	@pickle case class SetCameraLocation(x: Double, y: Double) extends GameMessage
	@pickle case class SetCameraFollow(characterUID: UID) extends GameMessage
	@pickle case class SetCameraSmoothing(smoothing: Boolean) extends GameMessage
	@pickle case class SetCameraSpeed(pps: Double) extends GameMessage

	// Debug message
	@pickle sealed trait Severity
	object Severity {
		@pickle case object Verbose extends Severity
		@pickle case object Info extends Severity
		@pickle case object Warn extends Severity
		@pickle case object Error extends Severity
	}
	@pickle case class Debug(severity: Severity, args: Seq[String]) extends ServerMessage with SystemMessage
}
