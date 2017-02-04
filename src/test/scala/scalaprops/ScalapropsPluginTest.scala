package scalaprops

import sbt.complete.Parser

object ScalapropsPluginTest extends Scalaprops {

  private[this] def invokeParser(classes: Map[String, Set[String]]): Parser[ScalapropsPlugin.ScalapropsTest] = {
    val clazz = ScalapropsPlugin.getClass
    val obj = clazz.getField("MODULE$").get(null)
    val methodName = "createParser"
    val methods = clazz.getDeclaredMethods
    def methodNames = methods.map(_.getName).toList
    val method = methods.find(_.getName endsWith methodName).getOrElse(sys.error("not found " + methodName + " in " + methodNames))
    method.invoke(obj, classes).asInstanceOf[Parser[ScalapropsPlugin.ScalapropsTest]]
  }

  val parser = Property.forAll{
    val aaa = "com.example.AAA"
    val classes = Map(
      aaa -> Set("aa_xx_1", "aa_xx_2", "aa_yy"),
      "com.example.BBB" -> Set("ccc", "ddd")
    )
    val p = invokeParser(classes)

    def show(s: Set[String]) = s.map("\"" + _ + "\"").toList.sorted

    def test(input: String, completions: Set[String]) = {
      val actual = input.foldLeft(p){_ derive _}.completions(0).get.map(_.display)
      assert(actual == completions, s"\n${show(actual)}\n is not equals \n${show(completions)}")
    }

    val aaaMethods = classes(aaa)
    val keys = Set("minSize", "maxDiscarded", "maxSize", "timeout", "minSuccessful", "seed").map("--" + _ + "=")

    test("", Set(" "))
    test(" ", classes.keySet)
    test(" com", classes.keySet)
    test(" com.example.", classes.keySet)
    test(" com.example.A", Set("com.example.AAA"))
    test(" com.example.AAA", Set(" ", ""))
    test(" com.example.AAA ", keys ++ aaaMethods)
    test(" com.example.AAA -", keys)
    test(" com.example.AAA a", aaaMethods)
    test(" com.example.AAA aa_x", Set("aa_xx_1", "aa_xx_2"))
    test(" com.example.AAA aa_xx_1", Set(" ", ""))
    test(" com.example.AAA aa_xx_1 ", Set("aa_xx_2", "aa_yy") ++ keys)
    test(" com.example.AAA --max", Set("--maxDiscarded=", "--maxSize="))
    test(" com.example.AAA --maxSize=", Set("<maxSize: Unsigned Int>"))
    test(" com.example.AAA --seed", Set("--seed="))
    test(" com.example.AAA --seed=", Set("<seed: Long>"))
    test(" com.example.AAA --seed=12345", Set("<seed: Long>"))
    test(" com.example.AAA --seed=12345 ", keys ++ aaaMethods)
    true
  }
}
