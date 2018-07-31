scalapropsSettings

scalapropsVersion := System.getProperty("scalaprops.version")

val scala211 = "2.11.12"

scalaVersion := scala211

crossScalaVersions := scala211 :: "2.10.7" :: Nil

TaskKey[Unit]("checkParallelExecution") := {
  assert((parallelExecution in Test).value == false)
}

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
