package textgen

import scala.io.Source
import textgen.model._
import textgen.bracketysyntax.BracketySyntax

@main
def main(): Unit = 
  val env = BracketySyntax.parseEnv(readFromFile("help us quest.txt")).get
  val chooser = RandomChooser.createDefault()
  val textGen = new TextGeneratorFromEnv(env, RandomChooser.createDefault())

  for (i <- 1 to 10)
    println(textGen.generateDefined("default"))

end main
  
def readFromFile(fileName: String): String = 
  val bufferedSource = Source.fromFile(fileName)
  val script = bufferedSource.getLines().mkString("\n")
  bufferedSource.close()
  script
