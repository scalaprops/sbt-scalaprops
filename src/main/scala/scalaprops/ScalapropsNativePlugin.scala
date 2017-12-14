package scalaprops

import sbt._
import sbt.Keys._
import ScalapropsPlugin.autoImport._
import scala.scalanative.sbtplugin.ScalaNativePluginInternal
import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport._

object ScalapropsNativePlugin extends AutoPlugin {

  object autoImport {
    sealed abstract class WhenNotNativeEnv extends Product with Serializable
    object WhenNotNativeEnv {
      case object NoWarn extends WhenNotNativeEnv
      case object PrintWarn extends WhenNotNativeEnv
      case object ThrowError extends WhenNotNativeEnv
    }

    val ScalapropsNativeTest = config("scalapropsNativeTest").extend(Test)
    val scalapropsNativeWarnEnv = SettingKey[WhenNotNativeEnv](
      "scalapropsNativeWarnEnv",
      "select behavior when not scala-native environment. detect project setting error or sbt-scalaprops bug."
    )

    lazy val scalapropsNativeSettings = ScalapropsNativePlugin.settings
  }

  import autoImport._

  private[this] def escapeIfKeyword(s: String) = {
    if (ScalaKeywords.values.contains(s)) {
      "`" + s + "`"
    } else {
      s
    }
  }

  private[this] lazy val scalapropsNativeTestSettings =
    Defaults.compileSettings ++
    Defaults.testSettings ++ Seq(
      classDirectory := (classDirectory in Test).value,
      dependencyClasspath := (dependencyClasspath in Test).value,
      sourceGenerators += Def.task {
        val tests = (scalapropsTestNames in Test).value.toList.sortBy(_._1).flatMap{
          case (obj, methods) =>
            val o = obj.split('.').map(escapeIfKeyword).mkString("_root_.", ".", "")
            methods.toList.sorted.map{ m =>
              s"""      ("$m", convert(${o}.${escapeIfKeyword(m)}))"""
            }.mkString(
              s"""    test(\n      "$obj",\n      $o,\n      xs,\n      a,\n""",
              ",\n",
              "\n    )\n\n"
            )
        }.mkString

        val mainClassFullName = (selectMainClass in Test).value.getOrElse(defaultTestMain)

        val (mainPackage, mainClass) = mainClassFullName.split('.').toList match {
          case init :+ last =>
            (Some(init), last)
          case clazz :: Nil =>
            (None, clazz)
          case Nil =>
            sys.error("invalid native test main class name " + mainClassFullName)
        }

        val pack = mainPackage.fold("")("package " + _.map(escapeIfKeyword).mkString(".") + "\n\n")

        val warn = scalapropsNativeWarnEnv.value match {
          case WhenNotNativeEnv.NoWarn =>
            ""
          case WhenNotNativeEnv.PrintWarn =>
            "warnIfNotNativeEnvironment()"
          case WhenNotNativeEnv.ThrowError =>
            "throwIfNotNativeEnvironment()"
        }

        val src = s"""${pack}object ${escapeIfKeyword(mainClass)} extends _root_.scalaprops.NativeTestHelper {
        |  def main(args: _root_.scala.Array[_root_.java.lang.String]): _root_.scala.Unit = {
        |    ${warn}
        |    val xs = _root_.scalaprops.Arguments.objects(args.toList)
        |    val a = _root_.scalaprops.Arguments.parse(args.toList)
        |$tests
        |    finish(a.showDuration)
        |  }
        |}
        |""".stripMargin

        val dir = (sourceManaged in Test).value
        val f = mainPackage.toList.flatten.foldLeft(dir)(_ / _) / (mainClass + ".scala")
        IO.write(f, src)
        Seq (f)
      }.taskValue,
      artifactPath in nativeLink := {
        crossTarget.value / (moduleName.value + "-test-out")
      },
      definedTests := (definedTests in Test).value
    )

  private[this] val defaultTestMain = "scalaprops.NativeTestMain"

  private[this] def runTest(binary: File, options: Seq[String]) = {
    val exitCode = scala.sys.process.Process(binary.getAbsolutePath +: options, None)
      .run(connectInput = true)
      .exitValue

    if (exitCode != 0) {
      sys.error("Nonzero exit code: " + exitCode)
    }
  }

  lazy val settings: Seq[Def.Setting[_]] = Seq(
    Seq(
      scalapropsNativeWarnEnv := WhenNotNativeEnv.ThrowError
    ),
    inConfig(ScalapropsNativeTest)(ScalaNativePluginInternal.scalaNativeConfigSettings),
    inConfig(ScalapropsNativeTest)(scalapropsNativeTestSettings),
    inConfig(Test)(Seq(
      nativeLink := (nativeLink in ScalapropsNativeTest).value,
      selectMainClass := Some(
        (selectMainClass in Test).value.getOrElse(defaultTestMain)
      ),
      test := {
        val binary = (nativeLink in ScalapropsNativeTest).value
        runTest(binary, Nil)
      },
      testOnly := {
        import Serialization.Implicits._
        val parser = loadForParser(definedTestNames)((s, i) => Defaults.testOnlyParser(s, i getOrElse Nil))
        Def.inputTaskDyn {
          val (selected, frameworkOptions) = parser.parsed
          Def.task {
            val binary = (nativeLink in ScalapropsNativeTest).value
            runTest(binary, selected ++ frameworkOptions)
          }
        }
      }.evaluated
    ))
  ).flatten
}
