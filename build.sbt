import sbtrelease._
import ReleaseStateTransformations._

scriptedBatchExecution := false

publishTo := (if (isSnapshot.value) None else localStaging.value)

val tagName = Def.setting {
  s"v${if (releaseUseGlobalVersion.value) (ThisBuild / version).value else version.value}"
}

val tagOrHash = Def.setting {
  if (isSnapshot.value) sys.process.Process("git rev-parse HEAD").lineStream_!.head
  else tagName.value
}

scalapropsSettings

crossScalaVersions += "3.8.2"

libraryDependencies ++= {
  scalaBinaryVersion.value match {
    case "3" =>
      Nil
    case _ =>
      Seq(
        Defaults.sbtPluginExtra(
          m = "org.scala-native" % "sbt-scala-native" % nativeVersion % "provided",
          sbtV = (pluginCrossBuild / sbtBinaryVersion).value,
          scalaV = (pluginCrossBuild / scalaBinaryVersion).value
        )
      )
  }
}

scalapropsVersion := "0.10.0"

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" =>
      (pluginCrossBuild / sbtVersion).value
    case _ =>
      "2.0.0-RC9"
  }
}

TaskKey[Unit]("scriptedTestSbt2") := Def.taskDyn {
  val values = sbtTestDirectory.value
    .listFiles(_.isDirectory)
    .flatMap { dir1 =>
      dir1.listFiles(_.isDirectory).map { dir2 =>
        dir1.getName -> dir2.getName
      }
    }
    .toList
  val args = values.filter {
    case ("native", _) => false
    case ("test", "basic") => false
    case _ => true
  }.collect { case (x1, x2) =>
    s"${x1}/${x2}"
  }
  val arg = args.mkString(" ", " ", "")
  streams.value.log.info("scripted" + arg)
  scripted.toTask(arg)
}.value

enablePlugins(SbtPlugin)

scriptedBufferLog := false

scriptedLaunchOpts ++= {
  val javaVmArgs = {
    import scala.collection.JavaConverters._
    java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
  }
  javaVmArgs.filter(a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith))
}

scriptedLaunchOpts ++= Seq(
  "-Dplugin.version=" + version.value,
  "-Dscala-native.version=" + nativeVersion,
  "-Dscalaprops.version=" + scalapropsVersion.value
)

startYear := Some(2015)

organization := "com.github.scalaprops"

name := "sbt-scalaprops"

description := "sbt plugin for scalaprops"

homepage := Some(url("https://github.com/scalaprops/sbt-scalaprops"))

licenses := Seq("MIT License" -> url("https://www.opensource.org/licenses/mit-license"))

commands += Command.command("updateReadme")(UpdateReadme.updateReadmeTask)

pomPostProcess := { node =>
  import scala.xml._
  import scala.xml.transform._
  def stripIf(f: Node => Boolean) = new RewriteRule {
    override def transform(n: Node) =
      if (f(n)) NodeSeq.Empty else n
  }
  val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
  new RuleTransformer(stripTestScope).transform(node)(0)
}

(Compile / doc / scalacOptions) ++= {
  Seq(
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath,
    "-doc-source-url",
    s"https://github.com/scalaprops/sbt-scalaprops/tree/${tagOrHash.value}€{FILE_PATH}.scala"
  )
}

pomExtra :=
  <developers>
    <developer>
      <id>xuwei-k</id>
      <name>Kenji Yoshida</name>
      <url>https://github.com/xuwei-k</url>
    </developer>
  </developers>
  <scm>
    <url>git@github.com:scalaprops/sbt-scalaprops.git</url>
    <connection>scm:git:git@github.com:scalaprops/sbt-scalaprops.git</connection>
    <tag>{tagOrHash.value}</tag>
  </scm>

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:existentials",
  "-language:implicitConversions",
)

scalacOptions ++= {
  scalaBinaryVersion.value match {
    case "3" =>
      Nil
    case _ =>
      Seq(
        "-language:higherKinds",
        "-Xlint",
      )
  }
}

releaseTagName := tagName.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("+ test"),
  setReleaseVersion,
  commitReleaseVersion,
  UpdateReadme.updateReadmeProcess,
  tagRelease,
  releaseStepCommandAndRemaining("+ publishSigned"),
  releaseStepCommandAndRemaining("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  UpdateReadme.updateReadmeProcess,
  pushChanges
)
