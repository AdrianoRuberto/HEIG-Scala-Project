package game.spells.icons

import engine.CanvasCtx

case class Dummy(label: String) extends SpellIcon {
	protected def drawIcon(ctx: CanvasCtx): Unit = {
		ctx.textAlign = "center"
		ctx.textBaseline = "middle"
		ctx.font = "400 12px 'Roboto Mono'"
		ctx.fillText(label, 30, 30)
	}
}
