package scalaprops

import sbt._, Keys._
import sbt.complete.{DefaultParsers, Parser}
import sbt.complete.DefaultParsers._
import scalaprops.Serialization.Implicits._
import scala.reflect.NameTransformer

object ScalapropsPlugin extends AutoPlugin {

  private[this] val LongBasic = mapOrFail('-'.? ~ Digit.+){
    case (neg, digits) =>
      (neg.toSeq ++ digits).mkString.toLong
  }

  private[this] def param[A](p: Parser[A], typeName: String): (String, (ScalapropsTest.Param, A) => ScalapropsTest.Param) => Parser[ScalapropsTest.Param] = {
    (key, f) =>
      Space ~> token(("--" + key + "=") ~> token(p, s"<${key}: ${typeName}>")).map { x =>
        f(ScalapropsTest.Param.Default, x)
      }
  }
  private[this] val long = param(LongBasic, "Long")
  private[this] val uint = param(DefaultParsers.NatBasic, "Unsigned Int")

  private[this] val Seed =
    long("seed", (p, x) => p.copy(seed = Some(x)))
  private[this] val MinSuccessful =
    uint("minSuccessful", (p, x) => p.copy(minSuccessful = Some(x)))
  private[this] val MaxDiscarded =
    uint("maxDiscarded", (p, x) => p.copy(maxDiscarded = Some(x)))
  private[this] val MinSize =
    uint("minSize", (p, x) => p.copy(minSize = Some(x)))
  private[this] val MaxSize =
    uint("maxSize", (p, x) => p.copy(maxSize = Some(x)))
  private[this] val Timeout =
    uint("timeout", (p, x) => p.copy(timeout = Some(x)))

  private[this] val ParamsParser: Parser[ScalapropsTest.Param] =
    Parser.oneOf(Seq(Seed, MinSuccessful, MaxDiscarded, MinSize, MaxSize, Timeout)).*.map(
      params => params.foldLeft(ScalapropsTest.Param.Default)(_ merge _)
    )

  private[this] val defaultParser: Parser[ScalapropsTest] = {
    (
      Space ~> token(StringBasic, _ => true) ~
     (Space ~> token(StringBasic, _ => true)).* ~
      ParamsParser
    ).map{
      case classNames Tuple2 methodNames Tuple2 param =>
        ScalapropsTest(classNames, methodNames, param)
    }
  }

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
      scalapropsTestNames := {
        scalapropsTestNames storeAs scalapropsTestNames triggeredBy (compile in Test)
      }.value,
      testFrameworks += new TestFramework("scalaprops.ScalapropsFramework"),
      parallelExecution in Test := false,
      scalapropsOnly := InputTask.createDyn(
        Defaults.loadForParser(scalapropsTestNames)(
          (state, classes) => classes.fold(defaultParser)(createParser)
        )
      ) {
        Def.task { test: ScalapropsTest =>
          (testOnly in Test).toTask((" " :: test.className :: "--" :: "--only" :: test.methodNames.toList).mkString(" "))
        }
      }.evaluated
    )

    val scalapropsSettings: Seq[Setting[_]] = scalapropsCoreSettings ++ Seq(
      libraryDependencies += "com.github.scalaprops" %% "scalaprops" % scalapropsVersion.value % "test"
    )

    val scalapropsWithScalazlaws: Seq[Setting[_]] = scalapropsSettings ++ Seq(
      libraryDependencies += "com.github.scalaprops" %% "scalaprops-scalaz" % scalapropsVersion.value % "test"
    )
  }

  final case class ScalapropsTest(
    className: String,
    methodNames: Seq[String],
    param: ScalapropsTest.Param
  )

  object ScalapropsTest {
    final case class Param(
      seed: Option[Long],
      minSuccessful: Option[Int],
      maxDiscarded: Option[Int],
      minSize: Option[Int],
      maxSize: Option[Int],
      timeout: Option[Int]
    ) {
      def merge(other: Param): Param = Param(
        seed = seed orElse other.seed,
        minSuccessful = minSuccessful orElse other.minSuccessful,
        maxDiscarded = maxDiscarded orElse other.maxDiscarded,
        minSize = minSize orElse other.minSize,
        maxSize = maxSize orElse other.maxSize,
        timeout = timeout orElse other.timeout
      )
    }
    object Param {
      val Default = Param(None, None, None, None, None, None)
    }
  }

  private[this] def createParser(tests: Map[String, Set[String]]): Parser[ScalapropsTest] = {
    tests.filter(_._2.nonEmpty).map { case (k, v) =>
      val (noChange, changed) = v.partition(n => NameTransformer.decode(n) == n)
      val all = noChange ++ changed.map(n => "\"" + NameTransformer.decode(n) + "\"")
      val parser = token(k) ~ distinctParser(all) ~ ParamsParser
      parser.map{
        case className Tuple2 methodName Tuple2 param =>
          ScalapropsTest(className, methodName, param)
      }
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
