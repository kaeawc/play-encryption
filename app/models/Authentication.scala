package models

import common._

import java.math.BigInteger
import java.security.SecureRandom

import scala.concurrent.{Await, Future, ExecutionContext}

trait Authentication[Model,Credentials] extends crypto.PBKDF2 {

  def authenticate(credentials:Credentials):Future[Option[Model]]

  def useSalt(plainText:String, salt:Array[Byte]):String = {
    hash(plainText, salt)
  }
}
