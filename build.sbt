name := "AleksChat"

version := "0.1"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  "io.netty" % "netty" % "3.10.6.Final"
)
