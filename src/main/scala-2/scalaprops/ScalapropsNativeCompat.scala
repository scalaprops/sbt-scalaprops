package scalaprops

private[scalaprops] trait ScalapropsNativeCompat { self: ScalapropsNativePlugin.type =>
  final val testFullKey = sbt.Keys.test
  final val testTaskSetting: Seq[sbt.Def.Setting[?]] =
    Nil
}
