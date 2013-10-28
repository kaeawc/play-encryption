package data

import models._

import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

import scala.util.{Random => Randomizer}

object Email {

  /**
   * Gives a random email
   */
  def random(first:String = FirstName.random,last:String = LastName.random) = {
    val domain = Domain.random

    (first + "." + last + "@" + domain + ".com").toLowerCase
  }

  /**
   * Gives a random email
   */
  def existing:String = {

    val users = Await.result(User.countAll, 5 seconds)

    val user = User.getById(Random.number(users.get.toInt))
    
    try {
      Await.result(user, 5 seconds) match {
        case Some(user:User) => user.email
        case _ => existing
      }
    } catch {
      case e:Exception => existing
    }
  }
}
