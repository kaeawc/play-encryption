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

  def parse(json:String) = {

    val session = Json.fromJson(Json.parse(json)).get

    Logger.info("Trying to parse a user session.")

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
        Logger.warn("User token not found in database")

        //TODO: finish implementing token series authentication to discover and warn users of credential thefts
        
        Future { None }
      }
    }
  }

  def invalidate(session:UserSession):Future[Option[UserSession]] = Future { 

    Logger.info("Invalidating an old user session")

    DB.withConnection { implicit connection =>
      SQL(
        """
          DELETE
          FROM user_session us
          WHERE us.user = {user}
            AND us.token = {token};
        """
      ).on(
        'user  -> session.user,
        'token -> session.token
      ).executeUpdate()
    }
  } flatMap {
    case rows:Int if rows > 1 => {
      Logger.info("Deleted more than one record.  Why wasn't the user + token combination unique?")

      Future { None }
    }
    case rows:Int if rows < 1 => {
      Logger.info("Deleted no records.  Could the record have already been deleted?")

      Future { None }
    }
    case 1 => {
      Logger.info("Invalided user token.")
      
      create(session.user)

    }
    case _ => {
      Logger.info("Unknown error")

      Future { None }

    }
  }

  def create(user:Long):Future[Option[UserSession]] = Future {

    Logger.info("Creating a new user session")

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

        Logger.info("Created user session")
        Some(UserSession(
          user,
          token
        ))
      case _ => None
    }
  }
}
