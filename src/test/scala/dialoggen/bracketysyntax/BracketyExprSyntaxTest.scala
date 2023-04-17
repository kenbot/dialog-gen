package dialoggen.bracketysyntax

import utest._

import dialoggen.model.{Expr, RefCase}

object BracketyExprSyntaxTest extends TestSuite {

  val tests = Tests {
    test("Empty script creates empty expr") {
      val result = BracketySyntax.parseExpr("")

      assert(result == Some(Expr.Empty))
    }

    test("Single word creates Expr.Word") {
      val result = BracketySyntax.parseExpr("Indisputably")

      assert(result == Some(Expr.Word("Indisputably")))
    }

    test("Words create Expr.And") {
      val result = BracketySyntax.parseExpr("how now brown cow")

      val expected = Expr.And(List(Expr.Word("how"), Expr.Word("now"), Expr.Word("brown"), Expr.Word("cow")))
      assert(result == Some(expected))
    }

    test("Parentheses create Expr.And") {
      val result = BracketySyntax.parseExpr("(how now brown cow)")
      val resultWhitespaceBefore = BracketySyntax.parseExpr("( how now brown cow)")
      val resultWhitespaceAfter = BracketySyntax.parseExpr("(how now brown cow )")

      val expected = Expr.And(List(Expr.Word("how"), Expr.Word("now"), Expr.Word("brown"), Expr.Word("cow")))
      assert(result == Some(expected))
      assert(resultWhitespaceBefore == Some(expected))
      assert(resultWhitespaceAfter == Some(expected))
    }

    test("Square brackets create Expr.Or") {
      val result = BracketySyntax.parseExpr("[a1 B2 c3]")
      val resultWhitespaceBefore = BracketySyntax.parseExpr("[ a1 B2 c3]")
      val resultWhitespaceAfter = BracketySyntax.parseExpr("[a1 B2 c3 ]")

      val expected = Expr.Or(List(Expr.Word("a1"), Expr.Word("B2"), Expr.Word("c3")))
      assert(result == Some(expected))
      assert(resultWhitespaceBefore == Some(expected))
      assert(resultWhitespaceAfter == Some(expected))
    }

    test("Curly braces create Expr.Opt") {
      val result = BracketySyntax.parseExpr("{whatever}")
      val resultWhitespaceBefore = BracketySyntax.parseExpr("{ whatever}")
      val resultWhitespaceAfter = BracketySyntax.parseExpr("{whatever }")

      val expected = Expr.Opt(Expr.Word("whatever"))
      assert(result == Some(expected))
      assert(resultWhitespaceBefore == Some(expected))
      assert(resultWhitespaceAfter == Some(expected))
    }

    test("Angle brackets create Expr.Ref") {
      val result = BracketySyntax.parseExpr("<the_other_thing>")
      val resultWhitespaceBefore = BracketySyntax.parseExpr("< the_other_thing>")
      val resultWhitespaceAfter = BracketySyntax.parseExpr("<the_other_thing >")

      val expected = Expr.Ref("the_other_thing", RefCase.AsWritten)
      assert(result == Some(expected))
      assert(resultWhitespaceBefore == Some(expected))
      assert(resultWhitespaceAfter == Some(expected))
    }

    test("Ref with identifier in all-caps chooses RefCase.AllCaps") {
      val result = BracketySyntax.parseExpr("<HELLO_EVERYONE>")

      val expected = Expr.Ref("hello_everyone", RefCase.AllCaps)
      assert(result == Some(expected))
    }

    test("Ref with identifier with only the first letter capitalised chooses RefCase.Capitalised") {
      val result = BracketySyntax.parseExpr("<Elephant_party>")

      val expected = Expr.Ref("elephant_party", RefCase.Capitalised)
      assert(result == Some(expected))
    }

    test("Ref with any other mix of lower and upper case chooses RefCase.AsWritten") {
      val result = BracketySyntax.parseExpr("<BaNAna>")

      val expected = Expr.Ref("banana", RefCase.AsWritten)
      assert(result == Some(expected))
    }

    test("Ref with non-identifier characters fails") {
      val result = BracketySyntax.parseExpr("<Wowee!>")
      assert(result == Some(Expr.Empty))
    }

    test("Multiple nested expressions parse correctly") {
      val result = BracketySyntax.parseExpr("[{a} (b)]")

      val expected = Expr.Or(List(Expr.Opt(Expr.Word("a")), Expr.And(List(Expr.Word("b")))))
      assert(result == Some(expected))
    }

    test("Opt can have multiple sub-expressions") {
      val result = BracketySyntax.parseExpr("{a b c}}")

      val expected = Expr.Opt(Expr.And(List(Expr.Word("a"), Expr.Word("b"), Expr.Word("c"))))
      assert(result == Some(expected))
    }
  }
}
