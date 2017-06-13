# Underwatch

Underwatch is a multiplayer realtime game built for the Scala course at HEIG-VD, using Play Framework, Akka Actors, Scala.js, Web Sockets and HTML5 Canvas.

![](http://i.imgur.com/41n18ZC.png)

## Gameplay

Once in a game, the player can move using standard "WASD" keys. Abilities may be used with the "E", "Q" and "Shift" keys or the primary mouse button (alternatively the space key is bound to the same action as the mouse button).

Each player has both a health and energy resources. Health is lost by getting hit by opponents, you die if it reaches 0. Energy is a regenerating resources used by you own abilities. The *Biotic Field* (healing zone) costs 50 energy and the *Sword* costs 30. The sprint will drain energy for as long as you are holding it active.

Two game modes are available:
	
 - **King of the Hill**: in this mode, a single control point is available in the center of the map. You can gain control of the objective by standing uncontested on it. The team in control of the objective will then slowly accumulate progress up to 100%. The first team to reach 100% wins. A win can only occur if the control point is uncontested, otherwise the progress will stop at 99% and an *Overtime* timer will start. This timer will start depleting as soon as the point is left uncontested, if it reaches 0, the team currently in control of the objective wins.
 
 - **Capture the Flag**: each team has a flag that they must defend while capturing the flag from the ennemy team. A flag can be taken by walking in the capture area visible under it. The ennemy flag must be brought back to your side of the map while your team is controlling its own flag to score a point. The first team at 3 points wins. If after a 5 minutes timer, no team has scored 3 points, the team with the most points wins. In case both teams have scored an equal amount of points, the game is a draw.

## Building and Running

The app can be run locally using `sbt server/run`, the only requirements are of course [SBT](http://www.scala-sbt.org/) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

SBT will take care of downloading any additional dependency and compile the application. Once the server is started, it will be available at [`http://localhost:9000`](http://localhost:9000).

Additionnal steps are required to build and run a distributable compiled version. In this case, take a look at the [Play doc](https://www.playframework.com/documentation/2.5.x/Production)!

### Using Docker

Alternatively, SBT can build a Docker image and publish it to the local Docker system. This is done by using

`sbt server/docker:publishLocal`.

In this case the application can be run using `docker run -p80:9000 underwatch:latest`. This will start a Docker container from the image and expose the TCP port 9000 as port 80 on the host network.

## Authors

[Bastien Clément](https://github.com/galedric) and [Adriano Ruberto](https://github.com/AdrianoRuberto)
