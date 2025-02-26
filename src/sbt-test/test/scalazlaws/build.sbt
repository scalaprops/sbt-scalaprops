scalapropsWithScalaz

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.12.20" :: "2.13.16" :: "3.3.4" :: Nil

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xlint",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)
