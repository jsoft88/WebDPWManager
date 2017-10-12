import sbt._

scalaVersion := "2.12.2"

name := "Persistence"

version := "1.0"

lazy val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion
)

//lazy val interaction = (project in file("Interaction"))

//lazy val persistence = (project in file("Persistence")).dependsOn(interaction).aggregate(interaction)