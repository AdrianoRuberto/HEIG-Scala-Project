package game.server.modes.twistingnether

import engine.geometry.Vector2D
import game.doodads.Doodad
import game.doodads.hud.KothStatusSkeleton
import game.server.BasicBot

class TwistingNetherBot(name: String) extends BasicBot(name: String) {
	var status: KothStatusSkeleton = null

	override def tick(dt: Double): Unit = {
		//log(status.capture.current)
	}

	var dest: Vector2D = Vector2D(0, 500)

	def move(x: Double, y: Double) = {

	}

	override def doodadCreated: DoodadHandler = {
		case Doodad.Hud.KothStatus(skeleton) => status = skeletons.getAs[KothStatusSkeleton](skeleton)
	}
}
