import sbtrelease._
import ReleaseStateTransformations._
import com.typesafe.sbt.pgp.PgpKeys

val tagName = Def.setting {
  s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}"
}

val tagOrHash = Def.setting {
  if (isSnapshot.value) sys.process.Process("git rev-parse HEAD").lines_!.head
  else tagName.value
}

scalapropsSettings

libraryDependencies ++= {
  if((sbtBinaryVersion in pluginCrossBuild).value.startsWith("1.0")) {
    Nil
  } else {
    Defaults.sbtPluginExtra(
      m = "org.scala-native" % "sbt-scala-native" % nativeVersion % "provided",
      sbtV = (sbtBinaryVersion in update).value,
      scalaV = (scalaBinaryVersion in update).value
    ) :: Nil
  }
}

scalapropsVersion := "0.5.1"

ScriptedPlugin.scriptedSettings

sbtPlugin := true

ScriptedPlugin.scriptedBufferLog := false

ScriptedPlugin.scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

ScriptedPlugin.scriptedLaunchOpts ++= Seq(
  "-Dscala-native.version=" + nativeVersion,
  "-Dplugin.version=" + version.value,
  "-Dscala-native.version=" + nativeVersion,
  "-Dscalaprops.version=" + scalapropsVersion.value
)

startYear := Some(2015)

organization := "com.github.scalaprops"

name := "sbt-scalaprops"

description := "sbt plugin for scalaprops"

homepage := Some(url("https://github.com/scalaprops/sbt-scalaprops"))

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

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

scalacOptions in (Compile, doc) ++= {
  Seq(
    "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
    "-doc-source-url", s"https://github.com/scalaprops/sbt-scalaprops/tree/${tagOrHash.value}€{FILE_PATH}.scala"
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

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)

def setSbtPluginCross(v: String) = "set sbtVersion in pluginCrossBuild := \"" + v + "\""
val SetSbt_0_13 = setSbtPluginCross("0.13.16")
val SetSbt_1 = setSbtPluginCross("1.0.1")

def crossSbtCommand(command: String): ReleaseStep = {
  val list = List(
    SetSbt_0_13,
    command,
    SetSbt_1,
    command
  )
  releaseStepCommandAndRemaining(list.mkString(";", ";", ""))
}

releaseTagName := tagName.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  crossSbtCommand("test"),
  releaseStepCommand(SetSbt_0_13),
  releaseStepCommand("scripted"),
  releaseStepCommand(SetSbt_1),
  releaseStepCommand("scripted test/*"),
  setReleaseVersion,
  commitReleaseVersion,
  UpdateReadme.updateReadmeProcess,
  tagRelease,
  crossSbtCommand("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  UpdateReadme.updateReadmeProcess,
  pushChanges
)

credentials ++= PartialFunction.condOpt(sys.env.get("SONATYPE_USER") -> sys.env.get("SONATYPE_PASSWORD")){
  case (Some(user), Some(password)) =>
    Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, password)
}.toList
