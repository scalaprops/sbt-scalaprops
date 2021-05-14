scalapropsSettings

scalapropsVersion := System.getProperty("scalaprops.version")

val scala211 = "2.11.12"

scalaVersion := scala211

crossScalaVersions := scala211 :: "2.12.13" :: "2.13.5" :: Nil

TaskKey[Unit]("checkParallelExecution") := {
  assert((parallelExecution in Test).value == false)
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
    (sourceDirectory in Test).value / "scala" / "Test2.scala",
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
