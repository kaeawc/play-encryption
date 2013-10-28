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
  series   : Long,
  created  : Date
)

object LoginAttempt {

  def create(user:Long,token:String,series:Long):Future[Option[LoginAttempt]] = {

    val created = new Date()

    Future {
      DB.withConnection { implicit connection =>
        SQL(
          """
            INSERT INTO user_session (
              user,
              token,
              series,
              created
            ) VALUES (
              {user},
              {token},
              {series},
              {created}
            );
          """
        ).on(
          'user     -> user,
          'token    -> token,
          'series   -> series,
          'created  -> created
        ).executeInsert()
      } match {
        case Some(id:Long) => 
          Some(LoginAttempt(
            id,
            user,
            token,
            series,
            created
          ))
        case _ => None
      }
    }
  }
}
