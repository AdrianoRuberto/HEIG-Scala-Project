# Underwatch

Underwatch is a multi-players realtime game built for the Scala course at HEIG-VD.

![](http://i.imgur.com/41n18ZC.png)

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
