package game.spells

import game.UID
import game.spells.effects.base.SpellContext
import scala.language.implicitConversions

package object effects {
	@inline implicit def UIDOpsFromSpellContext(uid: UID)
	                                           (implicit ctx: SpellContext): ctx.game.UIDOps = {
		new ctx.game.UIDOps(uid)
	}

	@inline implicit def iterableUIDOpsFromSpellContext(uids: Iterable[UID])
	                                                   (implicit ctx: SpellContext): ctx.game.IterableUIDOps = {
		new ctx.game.IterableUIDOps(uids)
	}
}
