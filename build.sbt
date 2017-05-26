import sbtrelease._
import ReleaseStateTransformations._
import com.typesafe.sbt.pgp.PgpKeys

scalapropsSettings

scalapropsVersion := "0.4.3"

def gitHash = scala.util.Try(
  sys.process.Process("git rev-parse HEAD").lines_!.head
).getOrElse("master")

ScriptedPlugin.scriptedSettings

sbtPlugin := true

ScriptedPlugin.scriptedBufferLog := false

ScriptedPlugin.scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)

ScriptedPlugin.scriptedLaunchOpts ++= Seq(
  "-Dplugin.version=" + version.value,
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
  val tag = if(isSnapshot.value) gitHash else { "v" + version.value }
  Seq(
    "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
    "-doc-source-url", s"https://github.com/scalaprops/sbt-scalaprops/tree/${tag}€{FILE_PATH}.scala"
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
    <tag>{if(isSnapshot.value) gitHash else { "v" + version.value }}</tag>
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

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  UpdateReadme.updateReadmeProcess,
  tagRelease,
  releaseStepTask(PgpKeys.publishSigned),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  UpdateReadme.updateReadmeProcess,
  pushChanges
)

credentials ++= PartialFunction.condOpt(sys.env.get("SONATYPE_USER") -> sys.env.get("SONATYPE_PASSWORD")){
  case (Some(user), Some(password)) =>
    Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, password)
}.toList
