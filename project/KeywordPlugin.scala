import sbt._
import Keys._

// https://github.com/sbt/contraband/blob/v0.3.0-M5/project/KeywordPlugin.scala
object KeywordPlugin extends AutoPlugin {
  override val requires = plugins.JvmPlugin

  lazy val scalaKeywords = TaskKey[Set[String]]("scala-keywords")
  lazy val generateKeywords = TaskKey[File]("generateKeywords")

  val scala3keywords = Set[String](
    "enum",
    "export",
    "given",
    "then",
  )

  def getScalaKeywords: Set[String] =
    {
      val g = new scala.tools.nsc.Global(new scala.tools.nsc.Settings)
      g.nme.keywords.map(_.toString)
    } ++ scala3keywords

  assert(getScalaKeywords.size == 55)

  def writeScalaKeywords(base: File, keywords: Set[String]): File =
    {
      val init = keywords.toList.sortBy(identity).map(tn => '"' + tn + '"').mkString("Set(", ", ", ")")
      val objectName = "ScalaKeywords"
      val packageName = "scalaprops"
      val keywordsSrc =
        s"""package $packageName
           |
           |private[$packageName] object $objectName {
           |  val values: Set[String] = $init
           |}""".stripMargin
      val out = base / packageName.replace('.', '/') / (objectName + ".scala")
      IO.write(out, keywordsSrc)
      out
    }
  override def projectSettings: Seq[Setting[_]] = inConfig(Compile)(Seq(
    scalaKeywords := getScalaKeywords,
    generateKeywords := writeScalaKeywords(sourceManaged.value, scalaKeywords.value),
    sourceGenerators += Def.task(Seq(generateKeywords.value)).taskValue
  ))
}
