addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.4.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
unmanagedSourceDirectories in Compile += baseDirectory.value.getParentFile / "src" / "main" / "scala"

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
