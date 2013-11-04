package play.api.mvc

import play.api.Logger
import play.api.libs.Crypto
import play.api.libs.json._
import models._
import common._
import scala.concurrent.duration._
import scala.concurrent.{Await,Future,ExecutionContext}
import ExecutionContext.Implicits.global

trait CookieManagement extends Configuration {

  /**
   * creates a Cookie instance with an encrypted value
   */
  def createCookie(key:String,value:String,secure:Boolean = false, rememberMe:Boolean = false) = {
    val expires:Option[Int] = if(rememberMe) Option(31536000) else None
    play.api.mvc.Cookie(key, Crypto.encryptAES(value), expires, "/", None, secure)
  }

  /**
   * creates a Cookie instance with an encrypted value
   */
  def createUserCookie(user:User):Future[Option[Cookie]] = {
    val expires = Option(31536000)

    UserSession.create(user.id) map {
      case Some(session:UserSession) => {
        val value = Crypto.encryptAES(Json.toJson(session).toString)
        Some(Cookie(userCookieKey, value, expires, "/", None, sslEnabled))
      }
      case _ => {
        Logger.error("Couldn't create a cookie for this user.")
        None
      }
    }
  }

  /**
   * reads a RequestHeader's Cookie by the given key if it exists
   */
  def readCookie(request:play.api.mvc.RequestHeader,key:String):String = {
    val cookie = request.cookies.get(key)
    cookie match {
      case Some(c:play.api.mvc.Cookie) => {
        implicit val salt:Array[Byte] = appSalt
        Crypto.decryptAES(c.value)
      }
      case _ => throw new Exception("Could not read key [" + key + "] from cookie in request.")
    }
  }

  /**
   * attempts to decode the current user's cookie
   */
  def getUserFromCookie(request:play.api.mvc.RequestHeader):Future[Option[User]] = {  
    
    try {

      val cookie = request.cookies.get(userCookieKey).get
      implicit val salt:Array[Byte] = appSalt
      val session = Crypto.decryptAES(cookie.value)

      UserSession.parse(session) flatMap {
        case Some(token:UserSession) => User.getById(token.user)
        case _ => Future { None }
      }
    } catch {
      case e:Exception => Future { None }
    }
  }
}
