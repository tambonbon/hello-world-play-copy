name := """hello-world-play-copy"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"
libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "com.h2database" % "h2" % "1.4.199",
  specs2 % Test,
  ws,
  "com.typesafe" % "config" % "1.4.0",
  "org.webjars" % "jquery" % "2.1.3",
  "com.beachape" %% "enumeratum" % "1.6.1",
  "com.beachape" %% "enumeratum-slick" % "1.6.0",

)

//libraryDependencies += "com.h2database" % "h2" % "1.4.197" % Test
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test
//libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.11"
//libraryDependencies += evolutions
//libraryDependencies += jdbc
//libraryDependencies += "com.typesafe" % "config" % "1.4.0"
//libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"
//libraryDependencies += specs2 % Test
//libraryDependencies += "org.webjars" % "jquery" % "2.1.3"
//libraryDependencies += "com.beachape" %% "enumeratum" % "1.6.1"
//libraryDependencies += "com.beachape" %% "enumeratum-slick" % "1.6.0"
//libraryDependencies ++= Seq(
//  "com.typesafe.play" %% "play-slick" % "5.0.0",
//  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
//  "com.beachape" %% "enumeratum-play-json" % "1.6.1"
//)
//libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"


//routesImport += "enums._"
//libraryDependencies +="com.typesafe.play" %% "play-slick-evolutions" % "1.0.0"
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
