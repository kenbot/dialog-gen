package textgen.model

import scala.compiletime.ops.string

trait TextGenerator:
  def generate(expr: Expr): String
  
  final def generateDefined(defId: DefinitionId): String = 
    generate(Expr.Ref(defId, RefCase.AsWritten))

end TextGenerator

object TextGeneratorFromEnv:
  def createEmpty(chooser: Chooser): TextGenerator = 
    new TextGeneratorFromEnv(ExprEnv.empty, chooser)

class TextGeneratorFromEnv(env: ExprEnv, chooser: Chooser) extends TextGenerator: 
  def generate(expr: Expr): String =
    val words = flattenExpr(expr)
    words.foldLeft("")((str, word) => str + spaced(word)).trim
    
  private def flattenExpr(expr: Expr): List[String] = expr match 
    case Expr.Word(text) => List(text)
    case Expr.And(exprs) => exprs.flatMap(flattenExpr)
    case Expr.Or(exprs) => chooser.chooseFrom(exprs).fold(Nil)(flattenExpr)
    case Expr.Opt(expr) => if chooser.coinToss() then flattenExpr(expr) else Nil
    case Expr.Ref(refId, refCase) => 
      val exprOpt = env.get(refId).map(_.body)
      exprOpt match 
        case None => Nil
        case Some(expr) => applyRefCase(refCase, flattenExpr(expr))
    case Expr.Empty => Nil

  private def isSentencePunctuation(c: Char): Boolean = 
    ",.?!;:".contains(c)

  private def spaced(word: String): String = 
    if word.forall(isSentencePunctuation) then word
    else s" $word"

  private def capitalise(str: String): String = 
    str match 
      case "" => ""
      case s => s.charAt(0).toUpper.toString + s.substring(1)

  private def applyRefCase(refCase: RefCase, words: List[String]): List[String] =
    refCase match 
      case RefCase.AsWritten => words
      case RefCase.Capitalised => words.headOption match 
        case Some(head) => capitalise(head) :: words.tail
        case None => Nil
      case RefCase.AllCaps => words.map(_.toUpperCase)

end TextGeneratorFromEnv