package game

import boopickle.DefaultBasic._

case class GameMode(name: String, desc: String)

object GameMode {
	implicit val pickler: Pickler[GameMode] = PicklerGenerator.generatePickler[GameMode]

	object CaptureTheFlag extends GameMode(
		"Capture the Flag",
		"Capture the enemy team's flag while defending yours"
	)
}
