package scalaprops

import sbt.*
import sbt.Keys.*
import ScalapropsPlugin.autoImport.*
import scala.reflect.NameTransformer
import scala.scalanative.sbtplugin.ScalaNativePluginInternal
import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.*

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

  import autoImport.*

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
        classDirectory := (Test / classDirectory).value,
        dependencyClasspath := (Test / dependencyClasspath).value,
        sourceGenerators += Def.task {
          val testNames = (Test / scalapropsTestNames).value.toList.sortBy(_._1).filter(_._2.nonEmpty)
          val tests = testNames.flatMap { case (obj, methods) =>
            val o = obj.split('.').map(escapeIfKeyword).mkString("_root_.", ".", "")
            methods.toList.sorted.map { m =>
              val methodName =
                if (NameTransformer.decode(m) == m) escapeIfKeyword(m) else s"`${NameTransformer.decode(m)}`"
              s"""      ("$m", convert(${o}.${methodName}))"""
            }.mkString(
              s"""    test(\n      "$obj",\n      $o,\n      xs,\n      a,\n""",
              ",\n",
              "\n    )\n\n"
            )
          }.mkString

          val mainClassFullName = "scalaprops.NativeTestMain"

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

          val src = if (testNames.nonEmpty) {
            s"""${pack}object ${escapeIfKeyword(mainClass)} extends _root_.scalaprops.NativeTestHelper {
          |  def main(args: _root_.scala.Array[_root_.java.lang.String]): _root_.scala.Unit = {
          |    ${warn}
          |    val xs = _root_.scalaprops.Arguments.objects(args.toList)
          |    val a = _root_.scalaprops.Arguments.parse(args.toList)
          |$tests
          |    finish(a.showDuration)
          |  }
          |}
          |""".stripMargin
          } else {
            s"""${pack}object ${escapeIfKeyword(mainClass)} {
          |  def main(args: _root_.scala.Array[_root_.java.lang.String]): _root_.scala.Unit = {
          |  }
          |}
          |""".stripMargin
          }

          val dir = (Test / sourceManaged).value
          val f = mainPackage.toList.flatten.foldLeft(dir)(_ / _) / (mainClass + ".scala")
          IO.write(f, src)
          Seq(f)
        }.taskValue,
        (nativeLink / artifactPath) := {
          crossTarget.value / (moduleName.value + "-test-out")
        },
        definedTests := (Test / definedTests).value
      )

  private[this] def runTest(binary: File, options: Seq[String]) = {
    val exitCode = scala.sys.process.Process(binary.getAbsolutePath +: options, None).run(connectInput = true).exitValue

    if (exitCode != 0) {
      sys.error("Nonzero exit code: " + exitCode)
    }
  }

  lazy val settings: Seq[Def.Setting[?]] = Seq(
    Seq(
      scalapropsNativeWarnEnv := WhenNotNativeEnv.ThrowError
    ),
    inConfig(ScalapropsNativeTest)(ScalaNativePluginInternal.scalaNativeConfigSettings(testConfig = true)),
    inConfig(ScalapropsNativeTest)(scalapropsNativeTestSettings),
    inConfig(Test)(
      Seq(
        test := {
          val binary = (ScalapropsNativeTest / nativeLink).value
          runTest(binary, Nil)
        },
        testOnly := {
          import sjsonnew.BasicJsonProtocol.*
          val parser = loadForParser(definedTestNames)((s, i) => Defaults.testOnlyParser(s, i getOrElse Nil))
          Def.inputTaskDyn {
            val (selected, frameworkOptions) = parser.parsed
            Def.task {
              val binary = (ScalapropsNativeTest / nativeLink).value
              runTest(binary, selected ++ frameworkOptions)
            }
          }
        }.evaluated
      )
    )
  ).flatten
}
