import sbt.Keys._
import sbt.Project.projectToRef

name := """HEIG-Scala-Project"""

version := "1.0-SNAPSHOT"

crossPaths := false

lazy val scalaOpts = Seq(
	//"-Xlog-implicits",
	"-feature",
	"-deprecation",
	"-Xfatal-warnings",
	"-unchecked",
	"-language:reflectiveCalls",
	"-language:higherKinds"
)

lazy val server = (project in file("server"))
	.settings(
		scalaVersion := "2.11.11",
		scalaJSProjects := Seq(client),
		pipelineStages in Assets := Seq(scalaJSPipeline),
		pipelineStages := Seq(digest, gzip),
		// triggers scalaJSPipeline when using compile or continuous compilation
		compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
		libraryDependencies ++= Seq(
			jdbc,
			cache,
			ws,
			"com.vmunier" %% "play-scalajs-scripts" % "0.5.0",
			"me.chrons" %% "boopickle" % "1.2.5"
		),
		scalacOptions ++= scalaOpts,
		includeFilter in gzip := "*.html" || "*.css" || "*.js" || "*.less"
	)
	.enablePlugins(PlayScala)
	.dependsOn(sharedJvm)

lazy val client = (project in file("client"))
	.settings(
		scalaVersion := "2.11.11",
		scalaSource in Compile := baseDirectory.value / "src",
		scalaJSUseMainModuleInitializer := true,
		libraryDependencies ++= Seq(
			"org.scala-js" %%% "scalajs-dom" % "0.9.2",
			"me.chrons" %%% "boopickle" % "1.2.5"
		),
		scalacOptions ++= scalaOpts
	)
	.enablePlugins(ScalaJSPlugin, ScalaJSPlay)
	.dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CustomCrossType) in file("shared"))
	.settings(
		scalaVersion := "2.11.11",
		scalacOptions ++= scalaOpts,
		crossPaths := false,
		libraryDependencies ++= Seq(
			"me.chrons" %%% "boopickle" % "1.2.5"
		)
	)
	.jvmSettings(
		scalaSource in Compile := baseDirectory.value / "src"
	)
	.jsSettings(
		scalaSource in Compile := baseDirectory.value / "src"
	)
	.jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

sources in(Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value