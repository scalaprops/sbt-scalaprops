package `yield` {

  import scalaprops._

  package `else` {
    object `for` extends Scalaprops {
      val `var` = Property.forAll(true)
    }
  }

  object `do` extends Scalaprops {
    val `match` = Property.forAll(true)
  }
}
