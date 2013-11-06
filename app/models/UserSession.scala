package models

import common._
import anorm._
import anorm.SqlParser._

import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._

import java.util.Date
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

case class UserSession(
  user     : Long,
  token    : String
)

object UserSession
extends ((
  Long,
  String
) => UserSession)
with crypto.Salt {
  
  val saltByteSize:Int = 255

  implicit val r = Json.reads[UserSession]
  implicit val w = Json.writes[UserSession]

  val tokens =
    long("user") ~
    str("token") map {
      case          user~token =>
        UserSession(user,token)
    }

  def fromJson(json:String) = {

    try {
      Some(Json.fromJson(Json.parse(json)).get)
    } catch {
      case e:Exception => None
    }
  }

  def parse(json:String) = {

    val session = fromJson(json).get

    Future {
      DB.withConnection { implicit connection =>
        SQL(
          """
            SELECT
              us.user,
              us.token
            FROM user_session us
            WHERE us.user = {user}
              AND us.token = {token};
          """
        ).on(
          'user -> session.user,
          'token -> session.token
        ).as(tokens.singleOpt)
      }
    } flatMap {
      case Some(session:UserSession) => invalidate(session)
      case _ => {
        Logger.warn("USERSESSION - User token not found in database")

        //TODO: finish implementing token series authentication to discover and warn users of credential thefts
        
        Future { None }
      }
    }
  }

  def invalidate(session:UserSession):Future[Option[UserSession]] = Future { 

    DB.withConnection { implicit connection =>
      SQL(
        """
          DELETE
          FROM user_session
          WHERE user = {user}
            AND token = {token};
        """
      ).on(
        'user  -> session.user,
        'token -> session.token
      ).executeUpdate()
    }
  } flatMap {
    case rows:Int if rows > 1 => {
      Logger.error("USERSESSION - Deleted more than one record.  Why wasn't the user + token combination unique?")

      Future { None }
    }
    case rows:Int if rows < 1 => {
      Logger.error("USERSESSION - Deleted no records.  Could the record have already been deleted?")

      Future { None }
    }
    case 1 => create(session.user)
    case _ => {
      Logger.error("USERSESSION - Unknown error")

      Future { None }

    }
  }

  def create(user:Long):Future[Option[UserSession]] = Future {

    val token   = bytes2hex(createSalt())
    val created = new Date()

    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO user_session (
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
        Some(UserSession(
          user,
          token
        ))
      case _ => {

        Logger.error("USERSESSION - Failed to create session: " + token.substring(0,10))

        None
      }
    }
  }
}
