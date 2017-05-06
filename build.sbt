name := "akka-cluster-router"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += Resolver.typesafeRepo("releases")

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies ++= Seq(
  "org.scalactic"               %% "scalactic"          % "3.0.1",
  "org.scalatest"               %% "scalatest"          % "3.0.1"   % "test",
  "com.typesafe.akka"           %% "akka-actor"         % "2.4.17",
  "com.typesafe.akka"           %% "akka-cluster"       % "2.4.17",
  "com.typesafe.akka"           %% "akka-slf4j"         % "2.4.17",
  "ch.qos.logback"              % "logback-classic"     % "1.1.7",
  "org.apache.commons"          %"commons-lang3"        %"3.5"
)

scalacOptions in Test ++= Seq("-Yrangepos")
        