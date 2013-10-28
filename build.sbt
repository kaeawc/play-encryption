name := "play-encryption"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.bouncycastle" % "bcprov-jdk15on" % "1.49",
  "net.iharder" % "base64" % "2.3.8"
)

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

play.Project.playScalaSettings
