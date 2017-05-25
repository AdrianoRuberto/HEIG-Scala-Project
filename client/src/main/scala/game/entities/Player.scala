package game.entities

import engine.geometry.Point

/**
  * Created by galedric on 26.05.2017.
  */
class Player extends Character(1) {
	color = "black"
	healthColor = "#5a5"

	override def update(dt: Double): Unit = {
		val Point(rx, ry) = engine.mouse.relative.point
		if (rx != 0 && ry != 0) tf = Math.atan2(ry, rx)
		super.update(dt)
	}
}
