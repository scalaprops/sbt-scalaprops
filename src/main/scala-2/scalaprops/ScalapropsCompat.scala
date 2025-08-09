package scalaprops

private[scalaprops] object ScalapropsCompat {
  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
