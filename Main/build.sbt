import sbt._

scalaVersion := "2.12.2"

name := "Main"

version := "1.0"

lazy val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion
)

//lazy val interaction = (project in file("Interaction"))

//lazy val persistence = (project in file("Persistence"))

//lazy val main = (project in file("Main")).dependsOn(interaction, persistence).aggregate(interaction, persistence)