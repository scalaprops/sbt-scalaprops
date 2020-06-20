package com.example

import scalaprops._
import scalajs.js
import scalajs.js.Dynamic

object Test1 extends Scalaprops {
  val fs = Dynamic.global.require("fs")

  def write(file: String, text: String) =
    fs.writeFileSync(file, text)

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
