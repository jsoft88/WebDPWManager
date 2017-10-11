import sbt._

name := "WebDPWManager"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.5.4"
      
lazy val `webdpwmanager` = (project in file(".")).enablePlugins(PlayScala).dependsOn(persistence, interaction).aggregate(persistence, interaction, main)

lazy val persistence = (project in file("Persistence")).dependsOn(interaction)

lazy val interaction = (project in file("Interaction"))

lazy val main = (project in file("Main")).dependsOn(interaction, persistence)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion, guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      