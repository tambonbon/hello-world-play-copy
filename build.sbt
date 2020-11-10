name := """hello-world-play-copy"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "com.h2database" % "h2" % "1.4.199",
  specs2 % Test,
  ws,
  "com.typesafe" % "config" % "1.4.0",
  "org.webjars" % "jquery" % "2.1.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
"org.scalatest" %% "scalatest" % "3.2.2",
  "org.mockito" %% "mockito-scala" % "1.16.0"
)
scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
