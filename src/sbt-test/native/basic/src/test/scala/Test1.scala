package com.example

import scalaprops.*

object Test1 extends Scalaprops {

  val `this is native env` = Property.forAll {
    (new Throwable).getStackTrace.foreach { s =>
      assert(s.getFileName == null, s.getFileName)
      assert(s.getLineNumber == 0, s.getLineNumber)
    }
    true
  }

}
