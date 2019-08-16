package com.example

import java.nio.charset.StandardCharsets
import scalaprops._
import java.nio.file.{Files, Paths}

object Test1 extends Scalaprops {

  def write(file: String, text: String) =
    Files.write(Paths.get(file), text.getBytes(StandardCharsets.UTF_8))

  val test1 = Property.forAll {
    write("test1.txt", "test1")
    true
  }

  val test2 = Property.forAll {
    write("test2.txt", "test2")
    true
  }

  val `test 3` = Property.forAll {
    write("test3.txt", "test3")
    true
  }

}
