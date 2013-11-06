package models

import anorm._
import anorm.SqlParser._

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._

import java.util.Date
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, ExecutionContext}
import ExecutionContext.Implicits.global

case class LoginAttempt(
  id       : Long,
  user     : Long,
  token    : String,
  created  : Date
)

object LoginAttempt {

  def create(user:Long,token:String):Future[Option[LoginAttempt]] = {

    val created = new Date()

    Future {
      DB.withConnection { implicit connection =>
        SQL(
          """
            INSERT INTO login_attempt (
              user,
              token,
              created
            ) VALUES (
              {user},
              {token},
              {created}
            );
          """
        ).on(
          'user     -> user,
          'token    -> token,
          'created  -> created
        ).executeInsert()
      } match {
        case Some(id:Long) => 
          Some(LoginAttempt(
            id,
            user,
            token,
            created
          ))
        case _ => None
      }
    }
  }
}
