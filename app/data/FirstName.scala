package data

import scala.util.{Random => Randomizer}

object FirstName {

  def random:String = list(Randomizer.nextInt(list.length))

  private val list = List(
      "Wally",
      "Long",
      "Tuan",
      "Gavin",
      "Monty",
      "Lonnie",
      "Dylan",
      "Rodrigo",
      "Gaylord",
      "Elvin",
      "Nolan",
      "Chong",
      "Ellsworth",
      "Nathanial",
      "Shad",
      "Domingo",
      "Palmer",
      "Dante",
      "Brain",
      "Bryon",
      "Daren",
      "Marcos",
      "Justin",
      "Scott",
      "Quincy",
      "Jarvis",
      "Bradford",
      "Sonny",
      "Fausto",
      "Douglas",
      "Francesco",
      "Rudy",
      "Adrian",
      "Sanford",
      "Patricia",
      "Asa",
      "Marion",
      "Derick",
      "Logan",
      "Nathanael",
      "Milton",
      "Eugenio",
      "Kevin",
      "Sebastian",
      "Jamal",
      "Cyrus",
      "Lee",
      "Mark",
      "Leonard",
      "Andy"
  )

}