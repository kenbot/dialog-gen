package dialoggen.model

import scala.util.Random

opaque type DefinitionId = String

object DefinitionId:
  def apply(id: String): DefinitionId = 
    id.toLowerCase

  given Conversion[String, DefinitionId] with
    def apply(str: String): DefinitionId = DefinitionId(str)

end DefinitionId

case class Definition(id: DefinitionId, body: Expr)

enum Expr: 
  case Word(text: String)
  case And(exprs: List[Expr])
  case Or(exprs: List[Expr])
  case Opt(expr: Expr)
  case Ref(id: DefinitionId, refCase: RefCase)
  case Empty

enum RefCase:
  case AsWritten
  case Capitalised
  case AllCaps