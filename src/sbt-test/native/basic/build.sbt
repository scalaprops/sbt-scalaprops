scalapropsCoreSettings

enablePlugins(ScalaNativePlugin)

name := "sbt-scalaprops-native-test1"

libraryDependencies ++= Seq(
  "com.github.scalaprops" %%% "scalaprops" % scalapropsVersion.value % "test",
  "com.github.scalaprops" %%% "scalaprops-scalaz" % scalapropsVersion.value % "test",
)

scalapropsNativeSettings

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := Seq("2.12.17", "2.13.10", "3.2.2")

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
