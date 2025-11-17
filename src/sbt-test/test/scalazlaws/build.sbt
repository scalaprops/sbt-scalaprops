scalapropsWithScalaz

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.13.18" :: "2.13.16" :: "3.3.6" :: Nil

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xlint",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)
