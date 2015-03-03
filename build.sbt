name := """shortener"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  ws,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "com.typesafe.play" %% "play-test" % "2.3.8" % "test"
)
