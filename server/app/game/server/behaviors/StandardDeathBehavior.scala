package game.server.behaviors

import engine.geometry.Vector2D
import game.UID
import game.doodads.Doodad
import game.server.{BasicGame, DoodadInstance}

trait StandardDeathBehavior extends BasicGame {

	def respawnLocation(player: UID): Vector2D
	def respawnTime(player: UID): Double = 5000.0
	def respawnCameraSpeed(player: UID): Double = 250.0

	def playerDeath(player: UID): Unit = ()

	private var doodads: Set[DoodadInstance] = Set.empty

	def removeDeathScreens(): Unit = {
		for (d <- doodads) d.remove()
		doodads = doodads.empty
	}

	createTicker { _ =>
		players.map(uid => uid -> uid.skeleton)
			.filter(!_._2.dead.value)
			.filter(_._2.health.current <= 0)
			.foreach { case (uid, player) =>
				uid.engine.disableInputs()
				playerDeath(uid)

				val deathScreen = createDoodad(Doodad.Hud.DeathScreen(respawnTime(uid)), uid)
				doodads += deathScreen

				val position = respawnLocation(uid)
				val time = respawnTime(uid)

				player.dead.value = true
				player.health.value = 1
				player.health.rate = player.health.max / time * 1000

				uid.camera.setSpeed((player.position <-> position) / time * 1000)
				uid.camera.move(position)

				player.x.value = -10000
				player.y.value = -10000

				schedule(time) {
					player.dead.value = false
					player.health.rate = 0
					player.health.value = player.health.max

					doodads -= deathScreen
					deathScreen.remove()

					uid.camera.move(position)
					uid.camera.setSpeed(respawnCameraSpeed(uid))
					uid.camera.followSelf()

					player.x.value = position.x
					player.y.value = position.y

					uid.engine.enableInputs()
				}
			}
	}
}
