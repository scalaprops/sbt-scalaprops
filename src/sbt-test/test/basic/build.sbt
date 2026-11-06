scalapropsSettings

scalapropsVersion := System.getProperty("scalaprops.version")

val scala212 = "2.12.21"

scalaVersion := scala212

crossScalaVersions := scala212 :: "2.13.18" :: "3.3.9" :: Nil

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

TaskKey[Unit]("checkParallelExecution") := {
  assert((Test / parallelExecution).value == false)
}

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:implicitConversions",
)

TaskKey[Unit]("generateTest2") := {
  IO.write(
    (Test / sourceDirectory).value / "scala" / "Test2.scala",
    """package com.example

import scalaprops._

object Test2 extends Scalaprops {
  property("x") = Property.forAll {
    Test1.write("test2-x.txt", "x")
    true
  }
}
"""
  )
}

InputKey[Unit]("test3txtExists") := {
  sbtBinaryVersion.value match {
    case "1.0" =>
      assert(file("test3.txt").isFile)
    case "2" =>
  }
}
