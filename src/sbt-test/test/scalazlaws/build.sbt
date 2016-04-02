scalapropsWithScalazlaws

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.11.8" :: "2.10.6" :: Nil

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
