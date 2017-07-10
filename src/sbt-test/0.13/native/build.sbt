scalapropsCoreSettings

enablePlugins(ScalaNativePlugin)

name := "sbt-scalaprops-native-test1"

libraryDependencies ++= (
  ("com.github.scalaprops" %%% "scalaprops" % scalapropsVersion.value % "test") ::
  ("com.github.scalaprops" %%% "scalaprops-scalazlaws" % scalapropsVersion.value % "test") ::
  Nil
)

scalapropsNativeSettings

scalapropsVersion := System.getProperty("scalaprops.version")

scalaVersion := "2.11.11"

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)

TaskKey[Unit]("checkTestMainClasses") := {
  val classes = (discoveredMainClasses in Test).value.toSet
  val expect = Set("example.OtherTestMain1", "example.OtherTestMain2", "scalaprops.NativeTestMain")
  assert(classes == expect, classes)
}
