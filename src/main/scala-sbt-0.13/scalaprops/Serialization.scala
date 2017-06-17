package scalaprops

private[scalaprops] object Serialization {
  object Implicits extends sbinary.DefaultProtocol {
    implicit def seqFormat[A: sbinary.Format]: sbinary.Format[Seq[A]] =
      sbt.Cache.seqFormat[A]
  }
}
