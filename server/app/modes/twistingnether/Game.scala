package modes
package twistingnether

class Game (roster: Seq[GameTeam]) extends BasicGame(roster) {

	def init(): Unit = ???
	def start(): Unit = ???

	def message: Receive = {
		case _ =>
	}
}
