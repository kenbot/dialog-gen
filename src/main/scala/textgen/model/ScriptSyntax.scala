package textgen.model


trait ScriptSyntax:
  def parseExpr(script: String): Option[Expr]
  def parseEnv(script: String):  Option[ExprEnv]
