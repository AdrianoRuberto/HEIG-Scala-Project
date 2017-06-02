package game.spells

import game.UID
import scala.language.implicitConversions

package object effects {
	@inline implicit def uidOpsFromSpellContext(uid: UID)(implicit ctx: SpellContext): ctx.game.UIDOps = {
		new ctx.game.UIDOps(uid)
	}
}
