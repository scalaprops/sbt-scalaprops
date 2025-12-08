scalapropsWithScalaz

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.12.21" :: "2.13.18" :: "3.3.7" :: Nil

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xlint",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
)
