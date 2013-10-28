package models

import anorm._
import anorm.SqlParser._
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

import play.api.Logger
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import play.api.libs.json._

import java.util.Date

case class Visit(
  id        : Long,
  uri       : String,
  userAgent : Option[String],
  user      : Option[Long],
  created   : Date
)

object Visit extends ((
  Long,
  String,
  Option[String],
  Option[Long],
  Date
) => Visit) with CookieManagement {

  implicit val r = Json.reads[Visit]
  implicit val w = Json.writes[Visit]

  val sqlResult =
    get[Long]("id") ~
    get[String]("uri") ~
    get[Option[String]]("user_agent") ~
    get[Option[Long]]("user") ~
    get[Date]("created")

  val a = sqlResult map {
    case   id~uri~userAgent~user~created =>
      Visit(id,uri,userAgent,user,created)
  }

  def create(request:RequestHeader):Future[Option[Visit]] = {

    val created = new Date()

    val uri = request.uri

    val userAgent = request.headers.get("User-Agent")

    

    getUserFromCookie(request) map {
      user =>
      val userId = user match {
        case Some(user:User) => Some(user.id)
        case _ => None
      }

      DB.withConnection { implicit connection =>
        val id = SQL(
          s"""
            INSERT INTO visit (
              uri,
              userAgent,
              user,
              created
            ) VALUES (
              {uri},
              {userAgent},
              {user},
              {created}
            );
          """
        ).on(
          'uri       -> uri,
          'userAgent -> userAgent,
          'user      -> userId,
          'created   -> created
        ).executeInsert()

        id match {
          case Some(id:Long) => 
            Some(Visit(id,uri,userAgent,userId,created))
          case _ =>
            None
        }
      }
    }
  }
}
