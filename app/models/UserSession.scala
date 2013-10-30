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
  token    : String,
  series   : Long
)

object UserSession
extends ((
  Long,
  String,
  Long
) => UserSession)
with crypto.Salt {
  
  val saltByteSize:Int = 255

  implicit val r = Json.reads[UserSession]
  implicit val w = Json.writes[UserSession]

  val tokens =
    long("user") ~
    str("token") ~
    long("series") map {
      case          user~token~series =>
        UserSession(user,token,series)
    }

  def parse(json:String) = Future {

    val session = Json.fromJson(Json.parse(json)).get

    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            a.user,
            a.token,
            a.series
          FROM user_session a
          WHERE user = {user}
            AND token = {token}
            AND series = {series};
        """
      ).on(
        'user -> session.user,
        'token -> session.token,
        'series -> session.series
      ).as(tokens.singleOpt) match {
        case Some(session:UserSession) => {

          // Logger.info("Authenticated User")
          
          invalidate(session)
          Some(session)
        }
        case _ => {
          Logger.warn("User token not found in database")

          //TODO: finish implementing token series authentication to discover and warn users of credential thefts
          
          None
        }
      }
    }
  }

  def invalidate(session:UserSession):Future[Option[UserSession]] = Future { None }

  def nextSeries(user:Long,isNew:Boolean):Future[Option[Long]] = Future {
    DB.withConnection { implicit connection =>
      val result = SQL(
        """
          SELECT
            CASE 
              WHEN MAX(a.series) IS NULL
              THEN 0
              ELSE MAX(a.series)
            END last_series
          FROM user_session a
          WHERE user = 1;
        """
      ).on(
        'user -> user
      ).apply()

      try {
        val lastSeries = result.head[Long]("last_series")

        if (isNew)
          Some(lastSeries + 1)
        else
          Some(lastSeries)
      } catch {
        case e:Exception => {
          Logger.error("Could not determine the next series number for this user's session.")
          None
        }
      }
    }
  }

  def create(user:User,series:Long):Future[Option[UserSession]] = Future {

    val token   = bytes2hex(createSalt())
    val created = new Date()

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
        'user     -> user.id,
        'token    -> token,
        'series   -> series,
        'created  -> created
      ).executeInsert()
    } match {
      case Some(id:Long) => 
        Some(UserSession(
          user.id,
          token,
          series
        ))
      case _ => None
    }
  }
}
