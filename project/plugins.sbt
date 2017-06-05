// Comment to get more information during initialization
logLevel := Level.Warn

// Resolvers
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")

// Play
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.14")

// SBT Web
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.4")

// ScalaJS
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.17")
