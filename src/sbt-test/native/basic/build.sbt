scalapropsCoreSettings

enablePlugins(ScalaNativePlugin)

name := "sbt-scalaprops-native-test1"

libraryDependencies ++= (
  ("com.github.scalaprops" %%% "scalaprops" % scalapropsVersion.value % "test") ::
  ("com.github.scalaprops" %%% "scalaprops-scalaz" % scalapropsVersion.value % "test") ::
  Nil
)

scalapropsNativeSettings

scalapropsVersion := System.getProperty("scalaprops.version")

scalaVersion := "2.11.12"

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
