version := "1.0"

scalaVersion := "2.12.2"

name := "Interaction"

lazy val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)