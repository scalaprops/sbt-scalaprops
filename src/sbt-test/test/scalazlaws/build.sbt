scalapropsWithScalazlaws

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.11.6" :: "2.10.4" :: Nil

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
