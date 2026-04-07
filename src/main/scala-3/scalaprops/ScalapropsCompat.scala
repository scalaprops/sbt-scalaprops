package scalaprops

import java.io.File
import xsbti.FileConverter
import xsbti.VirtualFileRef
import sbt.protocol.testing.TestResult

private[scalaprops] object ScalapropsCompat {
  inline def fileToVirtualFileRef(f: File, converter: FileConverter): VirtualFileRef =
    converter.toVirtualFile(f.toPath)

  inline def virtualFileRefToFile(ref: VirtualFileRef, converter: FileConverter): File =
    converter.toPath(ref).toFile

  inline def testResult: TestResult = TestResult.Passed
}
