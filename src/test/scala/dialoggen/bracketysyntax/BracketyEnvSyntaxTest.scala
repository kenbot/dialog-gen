package dialoggen.bracketysyntax



import utest._

import dialoggen.model.{Expr, RefCase, ExprEnv, Definition}

object BracketyEnvSyntaxTest extends TestSuite {

  val tests = Tests {
    
    test("Empty script creates empty env") {
      val result = BracketySyntax.parseEnv("")
      assert(result == Some(ExprEnv.empty))
    }

    test("Empty lines are skipped") {
      val result = BracketySyntax.parseEnv("\n foo=bar\n \n\nblab=flab  \n")
      val proof1 = result.flatMap(_.get("foo"))
      val proof2 = result.flatMap(_.get("blab"))

      assert(proof1 == Some(Definition("foo", Expr.Word("bar"))))
      assert(proof2 == Some(Definition("blab", Expr.Word("flab"))))
    }

    test("Named assignment puts it in the env") {
      val result = BracketySyntax.parseEnv("grasshopper = 123")
      val proof = result.flatMap(_.get("grasshopper"))

      assert(proof == Some(Definition("grasshopper", Expr.Word("123"))))
    }

    test("DefinitionIds are case insensitive") {
      val result = BracketySyntax.parseEnv("bLaH = bloo")
      val proof = result.flatMap(_.get("BlAh"))

      assert(proof == Some(Definition("blah", Expr.Word("bloo"))))
    }

    test("Named assignment are read on consecutive lines") {
      val result = BracketySyntax.parseEnv("medal = gold\nhead = shaved")
      val proof1 = result.flatMap(_.get("medal"))
      val proof2 = result.flatMap(_.get("head"))

      assert(proof1 == Some(Definition("medal", Expr.Word("gold"))))
      assert(proof2 == Some(Definition("head", Expr.Word("shaved"))))
    }

    test("Assignment still works without spaces") {
      val result = BracketySyntax.parseEnv("foo=bar")
      val proof = result.flatMap(_.get("foo"))

      assert(proof == Some(Definition("foo", Expr.Word("bar"))))
    }
  }
}
