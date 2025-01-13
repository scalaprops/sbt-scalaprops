package `yield` {

  import scalaprops.*

  package `else` {
    object `for` extends Scalaprops {
      val `var` = Property.forAll(true)
    }
  }

  object `do` extends Scalaprops {
    val `match` = Property.forAll(true)
  }
}
