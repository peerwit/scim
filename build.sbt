name := """lc"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5" // or "2.10.4"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "com.typesafe.play" %% "play-slick" % "0.8.1",
  "mysql" % "mysql-connector-java" % "5.1.6"
)

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)