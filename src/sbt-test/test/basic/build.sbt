scalapropsSettings

scalapropsVersion := System.getProperty("scalaprops.version")

val scala211 = "2.11.12"

scalaVersion := scala211

crossScalaVersions := scala211 :: "2.12.16" :: "2.13.8" :: Nil

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
