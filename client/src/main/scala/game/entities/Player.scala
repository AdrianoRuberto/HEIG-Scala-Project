package game.entities

import engine.geometry.Point

/**
  * Created by galedric on 26.05.2017.
  */
class Player extends Character(1) {

	// Energy
	var energy: Double = 25
	var energyMax: Double = 100
	var energyRate: Double = 15

	color = "black"
	healthColor = "#5a5"
	health = 133
	healthMax = 200

	override def update(dt: Double): Unit = {
		val Point(rx, ry) = engine.mouse.relative.point
		if (rx != 0 && ry != 0) tf = Math.atan2(ry, rx)

		// Update energy
		energy = energyMax min (energy + energyRate * dt / 1000)

		if (engine.mouse.left) {
			tx = engine.mouse.x
			ty = engine.mouse.y
		}

		super.update(dt)
	}
}
