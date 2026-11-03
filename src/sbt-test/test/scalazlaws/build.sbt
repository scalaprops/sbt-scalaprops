scalapropsWithScalaz

scalapropsVersion := System.getProperty("scalaprops.version")

crossScalaVersions := "2.12.21" :: "2.13.18" :: "3.3.9" :: Nil

scalacOptions ++= {
  scalaBinaryVersion.value match {
    case "2.12" =>
      Seq("-Xsource:3")
    case "2.13" =>
      Seq("-Xsource:3-cross")
    case "3" =>
      Nil
  }
}

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:implicitConversions",
)
