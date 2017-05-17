name := """HEIG-Scala-Project"""
version := "1.0-SNAPSHOT"
crossPaths := false

val scalaV = "2.11.11"

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
		scalaVersion := scalaV,
		scalacOptions ++= scalaOpts,
		scalaJSProjects := Seq(client),
		pipelineStages in Assets := Seq(scalaJSPipeline),
		pipelineStages := Seq(digest, gzip),
		compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
		libraryDependencies ++= Seq(
			jdbc,
			cache,
			ws,
			filters,
			"com.vmunier" %% "scalajs-scripts" % "1.0.0",
			"me.chrons" %% "boopickle" % "1.2.5"
		),
		includeFilter in gzip := "*.css" || "*.js"
	)
	.enablePlugins(PlayScala)
	.dependsOn(sharedJvm)

lazy val client = (project in file("client"))
	.settings(
		scalaVersion := scalaV,
		scalacOptions ++= scalaOpts,
		scalaJSUseMainModuleInitializer := true,
		crossPaths := false,
		libraryDependencies ++= Seq(
			"org.scala-js" %%% "scalajs-dom" % "0.9.2",
			"me.chrons" %%% "boopickle" % "1.2.5"
		)
	)
	.enablePlugins(ScalaJSPlugin, ScalaJSWeb)
	.dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
	.settings(
		scalaVersion := scalaV,
		scalacOptions ++= scalaOpts,
		crossPaths := false,
		libraryDependencies ++= Seq(
			"me.chrons" %%% "boopickle" % "1.2.5"
		)
	)
	.jvmSettings()
	.jsSettings()
	.jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

//sources in(Compile, doc) := Seq.empty
//publishArtifact in(Compile, packageDoc) := false
