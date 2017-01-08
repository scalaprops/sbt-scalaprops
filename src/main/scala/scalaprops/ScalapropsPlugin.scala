package scalaprops

import sbt._, Keys._
import sbt.complete.Parser
import sbt.complete.DefaultParsers._
import sbinary.DefaultProtocol._
import scala.reflect.NameTransformer

object ScalapropsPlugin extends AutoPlugin {

  private[this] val defaultParser: Parser[ScalapropsTest] = (
    Space ~> token(StringBasic, _ => true) ~
    (Space ~> token(StringBasic, _ => true)).*
  ).map(ScalapropsTest.tupled)

  object autoImport {
    val scalapropsTestNames = TaskKey[Map[String, Set[String]]]("scalapropsTestNames")
    val scalapropsOnly = InputKey[Unit]("scalapropsOnly")
    val scalapropsVersion = SettingKey[String]("scalapropsVersion")

    val scalapropsCoreSettings: Seq[Setting[_]] = Seq(
      scalapropsTestNames := {
        val loader = (testLoader in Test).value
        val runnerName = "scalaprops.ScalapropsRunner"
        getSingletonInstance(runnerName, loader) match {
          case Right(clazz) =>
            val instance = clazz.getField("MODULE$").get(null)
            val method = clazz.getMethod("testFieldNames", classOf[Class[_]])
            val testNames = (definedTestNames in Test).value
            testNames.map { testName =>
              val testClass = Class.forName(testName, true, loader)
              val testFields = method.invoke(instance, testClass).asInstanceOf[Array[String]]
              testName -> testFields.toSet
            }(collection.breakOut)
          case Left(e) =>
            streams.value.log.debug(runnerName + " could not found")
            Map.empty
        }
      },
      scalapropsTestNames <<= {
        // can't use := and .value
        // https://github.com/sbt/sbt/issues/1444
        scalapropsTestNames storeAs scalapropsTestNames triggeredBy (compile in Test)
      },
      testFrameworks += new TestFramework("scalaprops.ScalapropsFramework"),
      parallelExecution in Test := false,
      scalapropsOnly <<= InputTask.createDyn(
        Defaults.loadForParser(scalapropsTestNames)(
          (state, classes) => classes.fold(defaultParser)(createParser)
        )
      ) {
        Def.task { test =>
          (testOnly in Test).toTask((" " :: test.className :: "--" :: "--only" :: test.methodNames.toList).mkString(" "))
        }
      }
    )

    val scalapropsSettings: Seq[Setting[_]] = scalapropsCoreSettings ++ Seq(
      libraryDependencies += "com.github.scalaprops" %% "scalaprops" % scalapropsVersion.value % "test"
    )

    val scalapropsWithScalazlaws: Seq[Setting[_]] = scalapropsSettings ++ Seq(
      libraryDependencies += "com.github.scalaprops" %% "scalaprops-scalazlaws" % scalapropsVersion.value % "test"
    )
  }

  final case class ScalapropsTest(className: String, methodNames: Seq[String])

  private[this] def createParser(tests: Map[String, Set[String]]): Parser[ScalapropsTest] = {
    tests.filter(_._2.nonEmpty).map { case (k, v) =>
      val (noChange, changed) = v.partition(n => NameTransformer.decode(n) == n)
      val all = noChange ++ changed.map(n => "\"" + NameTransformer.decode(n) + "\"")
      val parser = token(k) ~ distinctParser(all)
      parser.map(ScalapropsTest.tupled)
    }.reduceOption(_ | _).map(Space ~> _).getOrElse(defaultParser)
  }

  private[this] def getSingletonInstance(objectName: String, loader: ClassLoader): Either[Throwable, Class[_]] =
    try {
      Right(Class.forName(objectName + "$", true, loader))
    } catch {
      case e: ClassNotFoundException =>
        Left(e)
    }

  private[this] def distinctParser(exs: Set[String]): Parser[Seq[String]] = {
    val base = token(Space) ~> token(NotSpace examples exs)
    base.flatMap { ex =>
      val (_, notMatching) = exs.partition(GlobFilter(ex).accept)
      distinctParser(notMatching).map { result => ex +: result }
    } ?? Nil
  }
}
