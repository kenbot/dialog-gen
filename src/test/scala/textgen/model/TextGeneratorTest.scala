package textgen.model

import utest._


object TextGeneratorTest extends TestSuite {

  val alwaysChooseTrue = new RiggedChooser(1, true)
  val alwaysChooseFalse = new RiggedChooser(1, false)

  val g: TextGenerator = 
    TextGeneratorFromEnv.createEmpty(alwaysChooseTrue)

  val tests = Tests {
    test("Word node generates text") {
      assert(g.generate(Expr.Word("abc")) == "abc")
    }

    test("And node generates space-separated text") {
      val expr = 
        Expr.And(List(
          Expr.Word("abc"), 
          Expr.Word("123"), 
          Expr.Word("xyz")))

      assert(g.generate(expr) == "abc 123 xyz")
    }

    test("Punctuation-only nodes absorb the space to the left") {
      val expr = 
        Expr.And(List(
          Expr.Word("abc"), 
          Expr.Word(","), 
          Expr.Word("xyz"),
          Expr.Word(".")))

      val result = g.generate(expr)

      assert(result == "abc, xyz.")
    }

    test("Empty node generates nothing") {
      val result = g.generate(Expr.Empty)
      assert(result == "")
    }

    test("Or node selects text from available choices") {
      val expr = 
        Expr.Or(List(
          Expr.Word("x"), 
          Expr.Word("yy"), 
          Expr.Word("zzz")))

      val result = g.generate(expr)
      val expectedFrom = List("x", "yy", "zzz")

      assert(expectedFrom.contains(result))
    }

    test("Or node with no choices returns empty") {
      val expr = 
        Expr.Or(Nil)

      assert(g.generate(expr) == "")
    }

    test("Opt node may or may not appear") {
      val expr = 
        Expr.Opt(Expr.Word("hello"))

      val gYes = TextGeneratorFromEnv.createEmpty(alwaysChooseTrue)
      assert(gYes.generate(expr) == "hello")

      val gNo = TextGeneratorFromEnv.createEmpty(alwaysChooseFalse)
      assert(gNo.generate(expr) == "")
    }

    test("Ref node selects named definition") {
      val expr = Expr.Ref("hi", RefCase.AsWritten)
      val env = ExprEnv.empty.define(Definition("hi", Expr.Word("CONTENT")))
      val textGen = new TextGeneratorFromEnv(env, alwaysChooseFalse)

      assert(textGen.generate(expr) == "CONTENT")
    }

    test("Ref node returns empty for unknown definition") {
      val expr = Expr.Ref("nosuchid", RefCase.AsWritten)
      val env = ExprEnv.empty
      val textGen = new TextGeneratorFromEnv(env, alwaysChooseFalse)

      assert(textGen.generate(expr) == "")
    }

    test("Ref node with 'Capitalised' case capitalises the first word") {
      val expr = Expr.Ref("ho", RefCase.Capitalised)
      val env = ExprEnv.empty.define(Definition("ho", Expr.Word("bing")))
      val textGen = new TextGeneratorFromEnv(env, alwaysChooseFalse)

      val result = textGen.generate(expr)

      assert(result == "Bing")
    }

    test("Ref node with 'Capitalised' case capitalises the first word, even if nested") {
      val expr = Expr.Ref("ho", RefCase.Capitalised)
      val ho = Definition("ho", Expr.And(List(Expr.Word("bing"), Expr.Word("bang"), Expr.Word("bong"))))
      val env = ExprEnv.empty.define(ho)
      val textGen = new TextGeneratorFromEnv(env, alwaysChooseFalse)

      val result = textGen.generate(expr)

      assert(result == "Bing bang bong")
    }

    test("Ref node with 'AllCaps' case capitalises everything") {
      val expr = Expr.Ref("ha", RefCase.AllCaps)
      val env = ExprEnv.empty.define(Definition("ha", Expr.Word("green")))
      val textGen = new TextGeneratorFromEnv(env, alwaysChooseFalse)

      val result = textGen.generate(expr)

      assert(result == "GREEN")
    }

    test("Ref node with 'AllCaps' case capitalises everything, even if nested") {
      val expr = Expr.Ref("ha", RefCase.AllCaps)
      val ha = Definition("ha", Expr.And(List(Expr.Word("green"), Expr.Word("yellow"), Expr.Word("blue"))))
      val env = ExprEnv.empty.define(ha)
      val textGen = new TextGeneratorFromEnv(env, alwaysChooseFalse)

      val result = textGen.generate(expr)

      assert(result == "GREEN YELLOW BLUE")
    }
  }
}

class RiggedChooser(riggedIndex: Int, riggedCoinToss: Boolean) extends Chooser:
  def chooseFrom[A](list: Seq[A]): Option[A] = 
    if riggedIndex >= 0 && riggedIndex < list.size then 
      Some(list(riggedIndex))
    else 
      None

  def coinToss(): Boolean = 
    riggedCoinToss

end RiggedChooser