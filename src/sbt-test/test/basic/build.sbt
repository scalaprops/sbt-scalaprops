scalapropsSettings

scalapropsVersion := System.getProperty("scalaprops.version")

val scala212 = "2.13.12"

scalaVersion := scala212

crossScalaVersions := scala212 :: "2.13.11" :: "3.3.0" :: Nil

TaskKey[Unit]("checkParallelExecution") := {
  assert((Test / parallelExecution).value == false)
}

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)

TaskKey[Unit]("generateTest2") := {
  IO.write(
    (Test / sourceDirectory).value / "scala" / "Test2.scala",
    """package com.example

import scalaprops._

object Test2 extends Scalaprops {
  val x = Property.forAll {
    Test1.write("test2-x.txt", "x")
    true
  }
}
"""
  )
}
