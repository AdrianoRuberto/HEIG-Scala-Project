name := """underwatch"""
version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.11"
crossPaths in ThisBuild := false

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
	resolvers += Resolver.sonatypeRepo("releases"),
	resolvers += Resolver.bintrayRepo("scalameta", "maven"),
	addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full),
	scalacOptions += "-Xplugin-require:macroparadise",
	scalacOptions in (Compile, console) := Seq(),
	scalacOptions ++= Seq(
		//"-Xlog-implicits",
		"-feature",
		"-deprecation",
		"-Xfatal-warnings",
		"-unchecked",
		"-language:reflectiveCalls",
		"-language:higherKinds",
		"-Xplugin-require:macroparadise"
	),
)

lazy val server = (project in file("server"))
	.settings(
		commonSettings,
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
	.dependsOn(sharedJvm, macros)

lazy val client = (project in file("client"))
	.settings(
		commonSettings,
		scalaJSUseMainModuleInitializer := true,
		libraryDependencies ++= Seq(
			"org.scala-js" %%% "scalajs-dom" % "0.9.2",
			"me.chrons" %%% "boopickle" % "1.2.5"
		)
	)
	.enablePlugins(ScalaJSPlugin, ScalaJSWeb)
	.dependsOn(sharedJs, macros)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
	.settings(
		name := "shared",
		commonSettings,
		libraryDependencies ++= Seq(
			"me.chrons" %%% "boopickle" % "1.2.5"
		)
	)
	.jsConfigure(_ enablePlugins ScalaJSWeb)
	.jsConfigure(_ dependsOn macros)
	.jvmConfigure(_ dependsOn macros)

lazy val macros = (project in file("macros"))
	.settings(
		commonSettings,
		libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0",
		libraryDependencies += "org.scalameta" %% "contrib" % "1.8.0"
	)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

//sources in(Compile, doc) := Seq.empty
//publishArtifact in(Compile, packageDoc) := false
