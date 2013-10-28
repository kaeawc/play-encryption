package data

import scala.util.{Random => Randomizer}

object Domain {

  def random:String = list(Randomizer.nextInt(list.length))

  private val list = List(
      "gmail",
      "yahoo",
      "live",
      "hotmail",
      "aol",
      "boingboing",
      "lavabit",
      "msn"
  )

}