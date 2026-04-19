package scalaprops

private[scalaprops] trait ScalapropsNativeCompat { self: ScalapropsNativePlugin.type =>
  final val testFullKey = sbt.Keys.testFull
  final val testTaskSetting: Seq[sbt.Def.Setting[?]] =
    Seq(
      sbt.Keys.test := testFullKey.value,
    )
}
