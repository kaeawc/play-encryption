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
  user  : Long,
  token : String,
  salt  : String
)

object UserSession
extends ((
  Long,
  String,
  String
) => UserSession)
with Authentication[UserSession,UserSession] {

  implicit val r = Json.reads[UserSession]
  implicit val w = Json.writes[UserSession]

  val tokens =
    long("user") ~
    str("token") ~
    str("salt") map {
      case          user~token~salt =>
        UserSession(user,token,salt)
    }

  def fromJson(json:String) = {

    try {
      Some(Json.fromJson(Json.parse(json)).get)
    } catch {
      case e:Exception => None
    }
  }

  def getByUser(user:Long) = Future {
    DB.withConnection { implicit connection =>
      SQL(
        """
          SELECT
            us.user,
            us.token,
            us.salt
          FROM user_session us
          WHERE us.user = {user}
          ORDER BY created DESC
          LIMIT 1;
        """
      ).on(
        'user -> user
      ).as(tokens.singleOpt)
    }
  }

  def parse(json:String) = authenticate(fromJson(json).get)

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

    val token              = createSalt()
    val salt               = createSalt()
    val hashedToken        = useSalt(token,salt)
    val created            = new Date()
    val storedSalt:String  = salt
    val cookieToken:String = token

    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO user_session (
            user,
            token,
            salt,
            created
          ) VALUES (
            {user},
            {token},
            {salt},
            {created}
          );
        """
      ).on(
        'user     -> user,
        'token    -> hashedToken,
        'salt     -> salt,
        'created  -> created
      ).executeInsert()
    } match {
      case Some(id:Long) =>
        Some(UserSession(
          user,
          cookieToken,
          storedSalt
        ))
      case _ => {

        Logger.error("Failed to create session: " + token.substring(0,10))

        None
      }
    }
  }
  
  def authenticate(session:UserSession):Future[Option[UserSession]] =

    getByUser(session.user) flatMap {
      case Some(valid:UserSession) => {

        val hashedToken = bytes2hex(useSalt(session.token,valid.salt))

        if (valid.token == hashedToken)
          invalidate(valid)
        else {

          Logger.warn("Login attempt made with invalid token - token hash does not match")

          Future { None }
        }
      }
      case _ => {

        Logger.warn("Login attempt made with invalid token - could not find any valid tokens for this user")

        //TODO: finish implementing token series authentication to discover and warn users of credential thefts
        
        Future { None }
      }
    }
}
