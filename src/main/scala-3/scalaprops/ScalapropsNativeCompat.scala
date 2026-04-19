package scalaprops

private[scalaprops] trait ScalapropsNativeCompat { self: ScalapropsNativePlugin.type =>
  final val testFullKey = sbt.Keys.testFull
}
