scalapropsWithScalaz

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.12.20" :: "2.13.14" :: "3.3.3" :: Nil

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
