package scalaprops

import java.io.File
import xsbti.FileConverter

private[scalaprops] object ScalapropsCompat {
  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }

  def fileToVirtualFileRef(f: File, converter: FileConverter): File =
    f

  def virtualFileRefToFile(f: File, converter: FileConverter): File =
    f

  def testResult: Unit = ()
}
