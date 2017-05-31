package game.protocol

import boopickle.Default._

package object enums {
	implicit val gameModePickler: Pickler[GameMode] = generatePickler[GameMode]
}
