package textgen.bracketysyntax

import textgen.model._

object BracketySyntax extends ScriptSyntax:
  def parseExpr(script: String): Option[Expr] = 
    BracketyParser.expr(script).toOption

  def parseEnv(script: String): Option[ExprEnv] = 
    BracketyParser.env(script).toOption