package dialoggen.model


object ExprEnv:
  val empty: ExprEnv = 
    ExprEnv(Map())

  def fromDefinitions(defs: Seq[Definition]): ExprEnv = 
    defs.foldLeft(empty)(_.define(_))
    
end ExprEnv

case class ExprEnv(private val dict: Map[DefinitionId, Definition]):

  final def definitions: List[Definition] = 
    dict.values.toList.sortBy(_.id.toString)

  def define(definition: Definition): ExprEnv = 
    ExprEnv(dict + (definition.id -> definition))

  def get(id: DefinitionId): Option[Definition] = 
    dict.get(id)
