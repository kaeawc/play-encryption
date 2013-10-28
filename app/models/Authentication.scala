package models

import common._

import java.math.BigInteger
import java.security.SecureRandom

import scala.concurrent.{Await, Future, ExecutionContext}

trait Authentication[Model] extends crypto.PBKDF2 {

  def authenticate(credentials:LoginCredentials):Future[Option[Model]]

  def useSalt(plainText:String, salt:Array[Byte]):String = {
    hash(plainText, salt)
  }
}
