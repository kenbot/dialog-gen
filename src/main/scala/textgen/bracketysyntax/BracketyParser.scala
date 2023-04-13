package textgen.bracketysyntax

import textgen.model.{Expr, RefCase, ExprEnv, Definition}

import textgen.{Parser, ParseResult}

object BracketyParser:

  private val specialSymbols = "[]{}<>()="

  def expr: Parser[Expr] = 
    singleExpr.many.map(collapseExprs)

  def env: Parser[ExprEnv] = 
    for 
      _ <- newLine.many
      defs <- definition.manySeparatedBy(newLine.oneOrMore)
    yield ExprEnv.fromDefinitions(defs)

  def definition: Parser[Definition] = 
    (for 
      name <- wordString
      _ <- Parser.expectChar('=').strip(sameLineWhitespace)
      e <- expr
    yield Definition(name, e)).strip(sameLineWhitespace)

  def newLine: Parser[Unit] = 
    Parser.expectChar('\n').strip(sameLineWhitespace).discard

  def singleExpr: Parser[Expr] = 
    and.orElse(or).orElse(opt).orElse(ref).orElse(word)
    
  def sameLineWhitespace: Parser[Unit] = 
    Parser.char.filter(c => c.isWhitespace && c != '\n').many.discard

  def wordString: Parser[String] = 
    wordChar.oneOrMore.asString

  def wordChar: Parser[Char] = 
    Parser.char.filter(c => !c.isWhitespace && !specialSymbols.contains(c))

  def word: Parser[Expr] = 
    wordString.map(Expr.Word(_)).strip(sameLineWhitespace)

  def identifierChar: Parser[Char] = 
    Parser.char.filter(c => c.isLetter || c.isDigit || c == '_')

  def identifier: Parser[String] = 
    identifierChar.oneOrMore.asString

  def and: Parser[Expr] = 
    (for 
      _ <- Parser.expectChar('(')
      exprs <- singleExpr.many.strip(sameLineWhitespace)
      _ <- Parser.expectChar(')')
    yield Expr.And(exprs)).strip(sameLineWhitespace)

  def or: Parser[Expr] = 
    (for 
      _ <- Parser.expectChar('[')
      exprs <- singleExpr.many.strip(sameLineWhitespace)
      _ <- Parser.expectChar(']')
    yield Expr.Or(exprs)).strip(sameLineWhitespace)

  def opt: Parser[Expr] = 
    (for 
      _ <- Parser.expectChar('{')
      e <- expr.strip(sameLineWhitespace) 
      _ <- Parser.expectChar('}')
    yield Expr.Opt(e)).strip(sameLineWhitespace)

  def ref: Parser[Expr] = 
    (for 
      _ <- Parser.expectChar('<')
      id <- identifier.strip(sameLineWhitespace) 
      _ <- Parser.expectChar('>')
    yield Expr.Ref(id, getRefCase(id))).strip(sameLineWhitespace)

  private def getRefCase(identifier: String): RefCase = 
    if (identifier.forall(c => !c.isLetter || c.isUpper)) RefCase.AllCaps
    else if (identifier.nonEmpty 
          && identifier(0).isUpper 
          && identifier.substring(1).forall(c => !c.isLetter || c.isLower)) RefCase.Capitalised
    else RefCase.AsWritten

  private def collapseExprs(exprs: List[Expr]): Expr = exprs match 
    case Nil => Expr.Empty
    case e :: Nil => e
    case es => Expr.And(es)

end BracketyParser
