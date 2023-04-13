package textgen

import textgen.model.{Expr, RefCase}


object Parser:
  def value[A](a: A): Parser[A] = 
    input => ParseResult.Found(a, input)

  def ok: Parser[Unit] = 
    value(())

  def notFound: Parser[Nothing] = 
    _ => ParseResult.NotFound

  def char: Parser[Char] = 
    case NonEmptyString(c, rest) => ParseResult.Found(c, rest)
    case "" => ParseResult.NotFound

  def expectChar(c: Char): Parser[Char] = 
    char.filter(_ == c)

  def remainingInput: Parser[String] = 
    input => ParseResult.Found(input, input)

  private object NonEmptyString:
    def unapply(str: String): Option[(Char, String)] = 
      str match 
        case "" => None
        case nonEmpty => Some((nonEmpty(0), nonEmpty.substring(1)))

end Parser

trait Parser[+A]:
  self =>  

  def apply(input: String): ParseResult[A]

  final def many: Parser[List[A]] = 
    (for 
      a <- self.expectConsumesInput
      as <- many
    yield a :: as).orValue(Nil)

  final def oneOrMore: Parser[List[A]] = 
    for 
      a <- self.expectConsumesInput
      as <- many
    yield a :: as
    
  final def oneOrMoreSeparatedBy(sep: Parser[_]): Parser[List[A]] = 
    for
      a <- self
      as <- sep.flatMap(_ => self).many
    yield a :: as

  final def manySeparatedBy(sep: Parser[_]): Parser[List[A]] = 
    oneOrMoreSeparatedBy(sep).orValue(Nil)

  final def orValue[B >: A](b: B): Parser[B] = 
    orElse(Parser.value(b))

  final def orElse[B >: A](parser2: Parser[B]): Parser[B] = 
    input => self(input) match         
      case result: ParseResult.Found[_] => result
      case ParseResult.NotFound => parser2(input)

  final def map[B](f: A => B): Parser[B] = 
    input => self(input).map(f)

  final def flatMap[B](f: A => Parser[B]): Parser[B] = 
    input => 
      self(input) match 
        case ParseResult.Found(a, remainingInput) => f(a)(remainingInput)
        case ParseResult.NotFound => ParseResult.NotFound

  final def filter(f: A => Boolean): Parser[A] = 
    input => self(input).filter(f)

  final def discard: Parser[Unit] = 
    map(_ => ())

  final def strip(junk: Parser[_]): Parser[A] = 
    for 
      _ <- junk.many
      a <- self
      _ <- junk.many
    yield a

  def asString(using A <:< List[Char]): Parser[String] = 
    map(chars => String(chars.toArray))

  private final def expectConsumesInput: Parser[A] = 
    for 
      remaining1 <- Parser.remainingInput
      a <- self
      remaining2 <- Parser.remainingInput
      consumedInput = (remaining2.length < remaining1.length)
      _ <- if (!consumedInput) Parser.notFound 
           else Parser.ok
    yield a

end Parser

enum ParseResult[+A]:
  case Found(a: A, remainingInput: String)
  case NotFound

  def toOption: Option[A] = this match
    case Found(a, _) => Some(a)
    case NotFound => None
  
  def map[B](f: A => B): ParseResult[B] = this match 
    case Found(a, remainingChars) => Found(f(a), remainingChars)
    case NotFound => NotFound

  def filter(f: A => Boolean): ParseResult[A] = this match
    case Found(a, _) => if (f(a)) this else NotFound
    case NotFound => NotFound

end ParseResult
