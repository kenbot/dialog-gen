package dialoggen.model

import scala.util.Random

trait Chooser:
  def chooseFrom[A](list: Seq[A]): Option[A]
  def coinToss(): Boolean

object RandomChooser:
  def createDefault(): Chooser = 
    new RandomChooser(new Random())


class RandomChooser(private val random: Random) extends Chooser:
  def chooseFrom[A](list: Seq[A]): Option[A] =
    if list.isEmpty then None
    else Some(list(random.nextInt(list.size)))

  def coinToss(): Boolean = 
    random.nextBoolean

end RandomChooser
