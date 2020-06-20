scalapropsCoreSettings

libraryDependencies += "com.github.scalaprops" %%% "scalaprops" % scalapropsVersion.value % "test"

enablePlugins(ScalaJSPlugin)

scalapropsVersion := System.getProperty("scalaprops.version")

scalaVersion := "2.13.2"
