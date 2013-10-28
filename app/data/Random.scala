package data

import scala.util.{ Random => RandomGen}

object Random {

  def string(length:Int):String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ ("-!£$").mkString("")
    (1 to length).map(
      x => {
        val index = RandomGen.nextInt(chars.length)
        chars(index)
      }
    ).mkString("")
  }

  def number(upTo:Int):Int = RandomGen.nextInt(upTo)
}
