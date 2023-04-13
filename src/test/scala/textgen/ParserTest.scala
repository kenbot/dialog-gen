package textgen

import scala.util.Try
import textgen.model._
import utest._
import textgen.{Parser, ParseResult}


object ParserTest extends TestSuite {
  val tests = Tests {

    test("Many parses many") {
      val result = Parser.char.many("hello").toOption
      assert(result == Some("hello".toList))
    }

    test("Filter restricts parsing results") {
      val foundResult = Parser.char.filter(_.isLower)("a").toOption
      val notFoundResult = Parser.char.filter(_.isLower)("A").toOption

      assert(foundResult == Some('a'))
      assert(notFoundResult == None)
    }

    test("Many returns empty list if no progress is being made") {
      val doesntConsumeInput = Parser.ok
      val result = doesntConsumeInput.many("whatevs").toOption
      assert(result == Some(Nil))
    }

    test("ManySeparatedBy parses many with separator") {
      val input = "a,b,c"
      val result = Parser.char.manySeparatedBy(Parser.expectChar(','))(input).toOption
      assert(result == Some(List('a', 'b', 'c')))
    }

    test("ManySeparatedBy returns empty list with no elements") {
      val input = ""
      val result = Parser.char.manySeparatedBy(Parser.expectChar(','))(input).toOption
      assert(result == Some(Nil))
    }

    test("OneOrMoreSeparatedBy parses many with separator") {
      val input = "a,b,c"
      val result = Parser.char.oneOrMoreSeparatedBy(Parser.expectChar(','))(input).toOption
      assert(result == Some(List('a', 'b', 'c')))
    }

    test("OneOrMoreSeparatedBy fails with no elements") {
      val input = ""
      val result = Parser.char.oneOrMoreSeparatedBy(Parser.expectChar(','))("").toOption
      assert(result == None)
    }

    test("Many returns empty list if there is no input") {
      val input = ""
      val result = Parser.char.many(input).toOption
      assert(result == Some(Nil))
    }

    test("OneOrMore parses many") {
      val result = Parser.char.oneOrMore("hello").toOption
      assert(result == Some("hello".toList))
    }

    test("OneOrMore fails if there is no input") {
      val result = Parser.char.oneOrMore("").toOption
      assert(result == None)
    }

    test("Split removes junk from either side of a parser") {
      val junk = Parser.char.filter(_.isLetter)
      val payload = Parser.char.filter(_.isDigit)
      val result = payload.strip(junk)("aBcdE9ZzYyXx").toOption
      assert(result == Some('9'))
    }
  }

}