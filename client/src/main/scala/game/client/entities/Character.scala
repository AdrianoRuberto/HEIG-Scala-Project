package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.{Drawable, Updatable}
import engine.geometry.Rectangle
import engine.utils.Layer

abstract class Character(val name: String, sublayer: Int = 0) extends Entity
		with Drawable with Updatable {

	children += new Nameplate(this)

	// Drawing layer
	val layer: Layer = Layer.Players / sublayer

	// Resources
	private var resources: List[Resource] = Nil
	protected def createResource(value: Double, max: Double,
	                             regen: Double = 0, smoothing: Boolean = false): Resource = {
		resources = new Resource(value, max, regen, smoothing) :: resources
		resources.head
	}

	// Health
	val health: Resource = createResource(100, 100, smoothing = true)
	var healthColor: String = "#f55"

	// Characteristics
	var size: Double = 30
	var color: String = "black"
	var skin: Int = 1
	var speed: Double = 100

	// Current position
	var x: Double = 250.0
	var y: Double = 250.0
	var f: Double = 0.0

	// Target position
	var tx: Double = 250.0
	var ty: Double = 250.0
	var tf: Double = 0.0

	// Bounding box of this character
	def boundingBox = Rectangle(x - size / 2, y - size / 2, size, size)

	def update(dt: Double): Unit = {
		// Update position
		if (tx != x || ty != y) {
			val dx = tx - x
			val dy = ty - y
			val a = Math.sqrt(dx * dx + dy * dy)
			val b = speed * (dt / 1000)
			val c = a min b
			x += dx / a * c
			y += dy / a * c
		}

		// Update facing
		if (tf != f) {
			val df = Math.atan2(Math.sin(tf - f), Math.cos(tf - f))
			if (Math.abs(df) < 0.1) f = tf
			else f += (df / 3)
		}

		// Update resources
		for (resource <- resources) resource.update(dt)
	}

	def setFacing(a: Double): Unit = tf = f

	def setPosition(a: Double, b: Double): Unit = {
		x = a
		y = b
		moveTo(a, b)
	}

	def moveTo(a: Double, b: Double): Unit = {
		tx = a
		ty = b
	}

	def draw(ctx: CanvasCtx): Unit = {
		val size_2 = size / 2
		ctx.translate(size_2, size_2)
		ctx.rotate(f - Math.PI / 2)
		ctx.translate(-size_2, -size_2)

		ctx.fillStyle = "#eee"
		ctx.fillRect(0, 0, size, size)

		ctx.fillStyle = healthColor
		ctx.fillRect(0, size, size, - size * health.smoothPercent)

		ctx.lineWidth = skin
		ctx.strokeStyle = color
		ctx.fillStyle = color

		ctx.strokeRect(0, 0, size, size)
		ctx.fillRect(size / 4, size - 10, 4, 4)
		ctx.fillRect(size / 4 * 3 - 4, size - 10, 4, 4)
	}
}
